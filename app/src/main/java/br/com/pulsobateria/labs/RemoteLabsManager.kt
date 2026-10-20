package br.com.pulsobateria.labs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import br.com.pulsobateria.monitor.BatteryRepository
import br.com.pulsobateria.monitor.MonitorService
import br.com.pulsobateria.core.LabMath
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.Collections
import java.util.UUID

private const val REMOTE_VERSION = 1

enum class RemoteRole { IDLE, HOST, CONTROLLER }

data class RemoteEndpoint(
    val host: String,
    val port: Int,
    val token: String,
    val deviceName: String,
) {
    fun toQrPayload(): String = buildString {
        append("pulso://labs-remote")
        append("?v=").append(REMOTE_VERSION)
        append("&host=").append(Uri.encode(host))
        append("&port=").append(port)
        append("&token=").append(Uri.encode(token))
        append("&name=").append(Uri.encode(deviceName))
    }

    companion object {
        fun fromPayload(payload: String): RemoteEndpoint? = runCatching {
            val uri = Uri.parse(payload.trim())
            require(uri.scheme == "pulso")
            require(uri.host == "labs-remote")
            require(uri.getQueryParameter("v")?.toIntOrNull() == REMOTE_VERSION)
            val host = uri.getQueryParameter("host")?.trim().orEmpty()
            val port = uri.getQueryParameter("port")?.toIntOrNull() ?: 0
            val token = uri.getQueryParameter("token")?.trim().orEmpty()
            val name = uri.getQueryParameter("name")?.trim().orEmpty().ifBlank { "Celular remoto" }
            require(isLocalIpv4(host) && token.matches(Regex("[0-9a-fA-F]{20,64}")) && port in 1024..65535)
            RemoteEndpoint(host, port, token, name)
        }.getOrNull()
    }
}

data class RemoteSnapshot(
    val deviceName: String,
    val batteryPercent: Int? = null,
    val currentMa: Double? = null,
    val temperatureC: Double? = null,
    val testElapsedMs: Long = 0L,
    val testRunning: Boolean = false,
    val chargingMs: Long = 0L,
    val notChargingMs: Long = 0L,
    val unknownMs: Long = 0L,
    val sky: String = "",
    val activeKind: String? = null,
    val activeId: Long? = null,
    val activeStatus: String? = null,
) {
    companion object {
        fun fromJson(json: JSONObject): RemoteSnapshot = RemoteSnapshot(
            deviceName = json.optString("deviceName", "Celular remoto"),
            batteryPercent = json.optInt("batteryPercent").takeIf { json.has("batteryPercent") && it >= 0 },
            currentMa = json.optDouble("currentMa").takeIf { json.has("currentMa") && it.isFinite() },
            temperatureC = json.optDouble("temperatureC").takeIf { json.has("temperatureC") && it.isFinite() },
            testElapsedMs = json.optLong("testElapsedMs", 0L),
            testRunning = json.optBoolean("testRunning", false),
            chargingMs = json.optLong("chargingMs", 0L),
            notChargingMs = json.optLong("notChargingMs", 0L),
            unknownMs = json.optLong("unknownMs", 0L),
            sky = json.optString("sky", ""),
            activeKind = json.optString("activeKind").ifBlank { null },
            activeId = json.optLong("activeId").takeIf { json.has("activeId") && it > 0L },
            activeStatus = json.optString("activeStatus").ifBlank { null },
        )
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("deviceName", deviceName)
        if (batteryPercent != null) put("batteryPercent", batteryPercent)
        if (currentMa != null) put("currentMa", currentMa)
        if (temperatureC != null) put("temperatureC", temperatureC)
        put("testElapsedMs", testElapsedMs)
        put("testRunning", testRunning)
        put("chargingMs", chargingMs)
        put("notChargingMs", notChargingMs)
        put("unknownMs", unknownMs)
        put("sky", sky)
        if (activeKind != null) put("activeKind", activeKind)
        if (activeId != null) put("activeId", activeId)
        if (activeStatus != null) put("activeStatus", activeStatus)
    }
}

data class RemoteLabsUiState(
    val role: RemoteRole = RemoteRole.IDLE,
    val hosting: Boolean = false,
    val hostEndpoint: RemoteEndpoint? = null,
    val controllerEndpoint: RemoteEndpoint? = null,
    val connected: Boolean = false,
    val snapshot: RemoteSnapshot? = null,
    val statusMessage: String = "",
    val actionMessage: String = "",
)

class RemoteLabsManager(
    private val context: Context,
    private val labs: LabController,
    private val repository: BatteryRepository,
    private val scope: CoroutineScope,
) {
    private val mutable = MutableStateFlow(RemoteLabsUiState())
    val ui = mutable.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var hostJob: Job? = null
    private var pollJob: Job? = null

    fun startHosting() {
        if (hostJob?.isActive == true) return
        hostJob = scope.launch(Dispatchers.IO) {
            val ip = localIpv4Address()
            if (ip == null) {
                mutable.value = mutable.value.copy(statusMessage = "Não foi possível descobrir o IP local. Conecte-se ao Wi‑Fi e tente novamente.")
                return@launch
            }
            val socket = ServerSocket(0, 16, InetAddress.getByName(ip))
            serverSocket = socket
            val endpoint = RemoteEndpoint(
                host = ip,
                port = socket.localPort,
                token = UUID.randomUUID().toString().replace("-", "").take(20),
                deviceName = Build.MODEL?.trim().orEmpty().ifBlank { "Celular A" },
            )
            mutable.value = mutable.value.copy(
                role = RemoteRole.HOST,
                hosting = true,
                hostEndpoint = endpoint,
                connected = false,
                controllerEndpoint = null,
                actionMessage = "QR Code pronto para pareamento na rede local.",
                statusMessage = "Conexão Wi‑Fi local, sem internet.",
            )
            try {
                while (isActive) {
                    val client = socket.accept()
                    scope.launch(Dispatchers.IO) { handleClient(client, endpoint) }
                }
            } catch (_: SocketTimeoutException) {
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                mutable.value = mutable.value.copy(statusMessage = "Servidor remoto encerrado: ${e.message}")
            } finally {
                runCatching { socket.close() }
                serverSocket = null
                mutable.value = mutable.value.copy(hosting = false, hostEndpoint = null)
            }
        }
    }

    fun stopHosting() {
        hostJob?.cancel()
        hostJob = null
        runCatching { serverSocket?.close() }
        serverSocket = null
        mutable.value = mutable.value.copy(role = RemoteRole.IDLE, hosting = false, hostEndpoint = null, connected = false, snapshot = null, statusMessage = "", actionMessage = "")
    }

    fun connect(payload: String) {
        val endpoint = RemoteEndpoint.fromPayload(payload)
        if (endpoint == null) {
            mutable.value = mutable.value.copy(statusMessage = "QR Code inválido ou incompatível.")
            return
        }
        pollJob?.cancel()
        mutable.value = mutable.value.copy(role = RemoteRole.CONTROLLER, controllerEndpoint = endpoint, statusMessage = "Conectando a ${endpoint.deviceName}…", actionMessage = "")
        pollJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val result = request(endpoint, JSONObject().put("type", "status").put("token", endpoint.token))
                val connected = result?.optBoolean("ok") == true
                val snapshot = if (connected) RemoteSnapshot.fromJson(result!!.getJSONObject("snapshot")) else null
                val message = when {
                    result == null -> "Sem resposta do outro celular. Confira se ambos estão na mesma rede."
                    connected -> "Conectado"
                    else -> result.optString("message", "Falha de conexão")
                }
                mutable.value = mutable.value.copy(connected = connected, snapshot = snapshot, statusMessage = message)
                delay(if (connected) 1500L else 2500L)
            }
        }
    }

    fun disconnectController() {
        pollJob?.cancel()
        pollJob = null
        mutable.value = mutable.value.copy(role = RemoteRole.IDLE, controllerEndpoint = null, connected = false, snapshot = null, actionMessage = "", statusMessage = "")
    }

    fun startRemoteSolar(sky: String, panelWatts: Int?, minutes: Int?) {
        val endpoint = mutable.value.controllerEndpoint ?: return
        scope.launch(Dispatchers.IO) {
            val payload = JSONObject().put("type", "startSolar").put("token", endpoint.token)
                .put("sky", sky)
            if (panelWatts != null) payload.put("panelWatts", panelWatts)
            if (minutes != null) payload.put("minutes", minutes)
            val response = request(endpoint, payload)
            val ok = response?.optBoolean("ok") == true
            mutable.value = mutable.value.copy(actionMessage = if (ok) "Teste solar remoto iniciado." else response?.optString("message") ?: "Falha ao iniciar teste remoto.")
        }
    }

    fun stopRemoteLab() {
        val endpoint = mutable.value.controllerEndpoint ?: return
        scope.launch(Dispatchers.IO) {
            val response = request(endpoint, JSONObject().put("type", "stopLab").put("token", endpoint.token))
            val ok = response?.optBoolean("ok") == true
            mutable.value = mutable.value.copy(actionMessage = if (ok) "Teste remoto finalizado." else response?.optString("message") ?: "Falha ao finalizar o teste remoto.")
        }
    }

    private suspend fun handleClient(socket: Socket, endpoint: RemoteEndpoint) {
        socket.use { client ->
            if (!mutable.value.hosting || mutable.value.hostEndpoint?.token != endpoint.token) return
            client.soTimeout = 5000
            val line = runCatching { BufferedReader(InputStreamReader(client.getInputStream(), Charsets.UTF_8)).readLine() }.getOrNull() ?: return
            val request = runCatching { JSONObject(line) }.getOrNull() ?: return
            val token = request.optString("token")
            val response = if (token != endpoint.token) {
                JSONObject().put("ok", false).put("message", "Pareamento rejeitado.")
            } else {
                mutable.value = mutable.value.copy(connected = true, actionMessage = "Controlador conectado pela rede local.")
                when (request.optString("type")) {
                    "hello", "status" -> JSONObject().put("ok", true).put("snapshot", localSnapshot().toJson())
                    "startSolar" -> runCatching {
                        ensureMonitorForRemote()
                        labs.start(
                            sky = request.optString("sky", "nublado").ifBlank { "nublado" },
                            panelWatts = request.optInt("panelWatts").takeIf { request.has("panelWatts") },
                            minutes = request.optInt("minutes").takeIf { request.has("minutes") },
                        )
                        JSONObject().put("ok", true).put("snapshot", localSnapshot().toJson())
                    }.getOrElse { error -> JSONObject().put("ok", false).put("message", error.message ?: "Falha ao iniciar teste remoto.") }
                    "stopLab" -> runCatching {
                        labs.stop()
                        JSONObject().put("ok", true).put("snapshot", localSnapshot().toJson())
                    }.getOrElse { error -> JSONObject().put("ok", false).put("message", error.message ?: "Falha ao encerrar o teste remoto.") }
                    else -> JSONObject().put("ok", false).put("message", "Comando remoto não reconhecido.")
                }
            }
            OutputStreamWriter(client.getOutputStream(), Charsets.UTF_8).use { writer ->
                writer.write(response.toString())
                writer.write("\n")
                writer.flush()
            }
        }
    }

    private suspend fun localSnapshot(): RemoteSnapshot {
        val now = System.currentTimeMillis()
        val reading = repository.reading.value.takeIf { it.updated in (now-90_000L)..now }
        val active = labs.snapshot()
        val total = active?.let { ((it.endedAt ?: now) - it.startedAt).coerceAtLeast(0L) } ?: 0L
        val chargingMs = active?.chargingMs ?: 0L
        val notChargingMs = active?.notChargingMs ?: 0L
        return RemoteSnapshot(
            deviceName = Build.MODEL?.trim().orEmpty().ifBlank { "Celular A" },
            batteryPercent = reading?.percent,
            currentMa = reading?.current,
            temperatureC = reading?.temperature,
            testElapsedMs = total,
            testRunning = active != null,
            chargingMs = chargingMs,
            notChargingMs = notChargingMs,
            unknownMs = LabMath.unknownMs(total, chargingMs, notChargingMs),
            sky = active?.sky.orEmpty(),
            activeKind = active?.kind,
            activeId = active?.id,
            activeStatus = active?.status,
        )
    }

    private suspend fun ensureMonitorForRemote() {
        if (repository.serviceActive.value) return
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            error("Permita notificações no celular monitorado antes do controle remoto.")
        }
        try {
            ContextCompat.startForegroundService(context, Intent(context, MonitorService::class.java))
        } catch (_: RuntimeException) {
            error("O Android bloqueou o início do monitoramento remoto. Abra o app no celular monitorado e tente novamente.")
        }
        val ok = withTimeoutOrNull(25_000L) { repository.serviceActive.first { it } } != null
        check(ok) { "Ative o monitoramento no celular remoto antes de iniciar o teste." }
    }

    private suspend fun request(endpoint: RemoteEndpoint, request: JSONObject): JSONObject? = withContext(Dispatchers.IO) {
        runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(endpoint.host, endpoint.port), 3500)
                socket.soTimeout = if (request.optString("type") == "startSolar") 32_000 else 4000
                val writer = OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8)
                writer.write(request.toString())
                writer.write("\n")
                writer.flush()
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
                val line = reader.readLine() ?: return@use null
                JSONObject(line)
            }
        }.getOrNull()
    }

    /** Host LAN apenas em IPv4 privado; não expor serviço de controle pela conexão celular. */
    private fun localIpv4Address(): String? = runCatching {
        Collections.list(NetworkInterface.getNetworkInterfaces())
            .asSequence()
            .filter { it.isUp && !it.isLoopback &&
                (it.name.startsWith("wlan", ignoreCase=true) ||
                 it.name.startsWith("ap", ignoreCase=true) ||
                 it.name.startsWith("swlan", ignoreCase=true)) }
            .flatMap { Collections.list(it.inetAddresses).asSequence() }
            .filterIsInstance<Inet4Address>()
            .mapNotNull { it.hostAddress }
            .firstOrNull(::isLocalIpv4)
    }.getOrNull()
}

private fun isLocalIpv4(host: String): Boolean {
    val segments = host.split('.')
    if (segments.size != 4) return false
    val parts = segments.map { value ->
        value.toIntOrNull()?.takeIf { it in 0..255 && (value == "0" || !value.startsWith('0')) }
            ?: return false
    }
    val a = parts[0]
    val b = parts[1]
    return a == 10 || (a == 172 && b in 16..31) || (a == 192 && b == 168)
}
