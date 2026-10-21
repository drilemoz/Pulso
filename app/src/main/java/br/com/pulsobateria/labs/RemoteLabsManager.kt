package br.com.pulsobateria.labs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.Manifest
import android.content.pm.PackageManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import android.os.Build
import androidx.core.app.NotificationCompat
import br.com.pulsobateria.MainActivity
import br.com.pulsobateria.R
import br.com.pulsobateria.core.LabMath
import br.com.pulsobateria.core.LabTimeForecast
import br.com.pulsobateria.data.LabTest
import br.com.pulsobateria.monitor.BatteryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
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
import java.security.MessageDigest
import java.util.Collections
import java.util.UUID
import java.util.concurrent.Semaphore

/** O protocolo v2 não reutiliza QR Code como credencial de comandos. */
private const val REMOTE_VERSION = 2
private const val QR_LIFETIME_MS = 300_000L
private const val ONLINE_WINDOW_MS = 12_000L
private const val NOTIFICATION_ID = 23_690
private const val CHANNEL_ID = "remote_labs_access"

/** A identidade exibida é declarada pelo próprio controlador, não autenticada por hardware. */
private fun displayName(value: String): String = value.trim().filterNot { Character.isISOControl(it) }
    .take(48).ifBlank { "Celular controlador" }
private fun newSecret() = UUID.randomUUID().toString().replace("-", "")
private fun sameSecret(a: String, b: String): Boolean = MessageDigest.isEqual(
    a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))

enum class RemoteRole { IDLE, HOST, CONTROLLER }

data class RemoteEndpoint(
    val host: String,
    val port: Int,
    val token: String,
    val deviceName: String,
) {
    fun toQrPayload(): String = buildString {
        append("pulso://labs-remote?v=").append(REMOTE_VERSION)
        append("&host=").append(Uri.encode(host))
        append("&port=").append(port)
        append("&token=").append(Uri.encode(token))
        append("&name=").append(Uri.encode(deviceName))
    }

    companion object {
        fun fromPayload(payload: String): RemoteEndpoint? = runCatching {
            require(payload.length in 50..512)
            val uri = Uri.parse(payload.trim())
            require(uri.scheme == "pulso" && uri.host == "labs-remote")
            require(uri.getQueryParameter("v")?.toIntOrNull() == REMOTE_VERSION)
            val host = uri.getQueryParameter("host")?.trim().orEmpty()
            val port = uri.getQueryParameter("port")?.toIntOrNull() ?: 0
            val token = uri.getQueryParameter("token")?.trim().orEmpty()
            val name = uri.getQueryParameter("name")?.trim().orEmpty()
            require(isLocalIpv4(host) && token.matches(Regex("[0-9a-fA-F]{32}")) && port in 1024..65535)
            RemoteEndpoint(host, port, token, displayName(name))
        }.getOrNull()
    }
}

data class RemoteSnapshot(
    val deviceName: String,
    val batteryPercent: Int? = null,
    val currentMa: Double? = null,
    val temperatureC: Double? = null,
    val testElapsedMs: Long = 0L,
    val targetMs: Long? = null,
    val startedAtMs: Long? = null,
    val deadlineAtMs: Long? = null,
    val endedAtMs: Long? = null,
    val estimatedFullAtMs: Long? = null,
    val testRunning: Boolean = false,
    val chargingMs: Long = 0L,
    val notChargingMs: Long = 0L,
    val unknownMs: Long = 0L,
    val sky: String = "",
    val activeKind: String? = null,
    val activeId: Long? = null,
    val activeStatus: String? = null,
    val initialPercent: Int? = null,
    val finalPercent: Int? = null,
    val initialTemperatureC: Double? = null,
    val finalTemperatureC: Double? = null,
    val meanTemperatureC: Double? = null,
    val maxTemperatureC: Double? = null,
    val stopAtFull: Boolean = false,
    val electricSource: String? = null,
) {
    companion object {
        fun fromJson(j: JSONObject) = RemoteSnapshot(
            deviceName = displayName(j.optString("deviceName", "Celular remoto")),
            batteryPercent = j.optInt("batteryPercent").takeIf { j.has("batteryPercent") && it in 0..100 },
            currentMa = j.optDouble("currentMa").takeIf { j.has("currentMa") && it.isFinite() },
            temperatureC = j.optDouble("temperatureC").takeIf { j.has("temperatureC") && it.isFinite() },
            testElapsedMs = j.optLong("testElapsedMs").coerceAtLeast(0),
            targetMs = j.optLong("targetMs").takeIf { j.has("targetMs") && it > 0 },
            startedAtMs = j.optLong("startedAtMs").takeIf { j.has("startedAtMs") && it > 0 },
            deadlineAtMs = j.optLong("deadlineAtMs").takeIf { j.has("deadlineAtMs") && it > 0 },
            endedAtMs = j.optLong("endedAtMs").takeIf { j.has("endedAtMs") && it > 0 },
            estimatedFullAtMs = j.optLong("estimatedFullAtMs").takeIf { j.has("estimatedFullAtMs") && it > 0 },
            testRunning = j.optBoolean("testRunning"),
            chargingMs = j.optLong("chargingMs").coerceAtLeast(0),
            notChargingMs = j.optLong("notChargingMs").coerceAtLeast(0),
            unknownMs = j.optLong("unknownMs").coerceAtLeast(0),
            sky = j.optString("sky", ""),
            activeKind = j.optString("activeKind").ifBlank { null },
            activeId = j.optLong("activeId").takeIf { j.has("activeId") && it > 0 },
            activeStatus = j.optString("activeStatus").ifBlank { null },
            initialPercent = j.optInt("initialPercent").takeIf { j.has("initialPercent") && it in 0..100 },
            finalPercent = j.optInt("finalPercent").takeIf { j.has("finalPercent") && it in 0..100 },
            initialTemperatureC = j.optDouble("initialTemperatureC").takeIf { j.has("initialTemperatureC") && it.isFinite() },
            finalTemperatureC = j.optDouble("finalTemperatureC").takeIf { j.has("finalTemperatureC") && it.isFinite() },
            meanTemperatureC = j.optDouble("meanTemperatureC").takeIf { j.has("meanTemperatureC") && it.isFinite() },
            maxTemperatureC = j.optDouble("maxTemperatureC").takeIf { j.has("maxTemperatureC") && it.isFinite() },
            stopAtFull = j.optBoolean("stopAtFull"),
            electricSource = j.optString("electricSource").ifBlank { null },
        )
    }

    fun toJson() = JSONObject().apply {
        put("deviceName", deviceName)
        if (batteryPercent != null) put("batteryPercent", batteryPercent)
        if (currentMa != null) put("currentMa", currentMa)
        if (temperatureC != null) put("temperatureC", temperatureC)
        put("testElapsedMs", testElapsedMs)
        if (targetMs != null) put("targetMs", targetMs)
        if (startedAtMs != null) put("startedAtMs", startedAtMs)
        if (deadlineAtMs != null) put("deadlineAtMs", deadlineAtMs)
        if (endedAtMs != null) put("endedAtMs", endedAtMs)
        if (estimatedFullAtMs != null) put("estimatedFullAtMs", estimatedFullAtMs)
        put("testRunning", testRunning)
        put("chargingMs", chargingMs)
        put("notChargingMs", notChargingMs)
        put("unknownMs", unknownMs)
        put("sky", sky)
        if (activeKind != null) put("activeKind", activeKind)
        if (activeId != null) put("activeId", activeId)
        if (activeStatus != null) put("activeStatus", activeStatus)
        if (initialPercent != null) put("initialPercent", initialPercent)
        if (finalPercent != null) put("finalPercent", finalPercent)
        if (initialTemperatureC != null) put("initialTemperatureC", initialTemperatureC)
        if (finalTemperatureC != null) put("finalTemperatureC", finalTemperatureC)
        if (meanTemperatureC != null) put("meanTemperatureC", meanTemperatureC)
        if (maxTemperatureC != null) put("maxTemperatureC", maxTemperatureC)
        put("stopAtFull", stopAtFull)
        if (electricSource != null) put("electricSource", electricSource)
    }
}

data class RemoteLabsUiState(
    val role: RemoteRole = RemoteRole.IDLE,
    val hosting: Boolean = false,
    /** Só existe enquanto há QR disponível para um novo pareamento. */
    val hostEndpoint: RemoteEndpoint? = null,
    val controllerEndpoint: RemoteEndpoint? = null,
    val connected: Boolean = false,
    val snapshot: RemoteSnapshot? = null,
    val connectedDevice: String? = null,
    val statusMessage: String = "",
    val actionMessage: String = "",
)

private data class RemoteGrant(
    val clientId: String,
    val sessionSecret: String,
    val deviceName: String,
    val issuedAt: Long,
    val lastSeenAt: Long,
)

private data class ControllerSession(
    val endpoint: RemoteEndpoint,
    val clientId: String,
    val sessionSecret: String,
)

class RemoteLabsManager(
    private val context: Context,
    private val labs: LabController,
    private val repository: BatteryRepository,
    private val scope: CoroutineScope,
) {
    private val mutable = MutableStateFlow(RemoteLabsUiState())
    val ui = mutable.asStateFlow()
    private val guard = Mutex()
    private val clientLimit = Semaphore(4)
    private var serverSocket: ServerSocket? = null
    private var hostJob: Job? = null
    private var heartbeatJob: Job? = null
    private var pollJob: Job? = null
    private var pairingIssuedAt: Long = 0L
    private var grant: RemoteGrant? = null
    private var controller: ControllerSession? = null
    private var sharedTestId: Long? = null

    fun startHosting() {
        if (hostJob?.isActive == true) return
        hostJob = scope.launch(Dispatchers.IO) {
            val ip = localIpv4Address()
            if (ip == null) {
                mutable.value = mutable.value.copy(statusMessage = "Conecte os dois aparelhos ao mesmo Wi-Fi antes de gerar o código.")
                return@launch
            }
            val socket = runCatching { ServerSocket(0, 8, InetAddress.getByName(ip)) }.getOrElse {
                mutable.value = mutable.value.copy(statusMessage = "Não foi possível abrir a conexão local: ${it.message}")
                return@launch
            }
            serverSocket = socket
            guard.withLock {
                grant = null
                sharedTestId = null
                val endpoint = newEndpoint(ip, socket.localPort)
                pairingIssuedAt = System.currentTimeMillis()
                mutable.value = RemoteLabsUiState(
                    role = RemoteRole.HOST, hosting = true, hostEndpoint = endpoint,
                    actionMessage = "QR Code válido por 5 minutos e para um único controlador.",
                    statusMessage = "Conexão local iniciada. Aguardando pareamento.",
                )
            }
            heartbeatJob = scope.launch(Dispatchers.IO) {
                while (isActive) {
                    delay(3000L)
                    guard.withLock {
                        val now = System.currentTimeMillis()
                        val active = grant
                        val online = active != null && now - active.lastSeenAt in 0..ONLINE_WINDOW_MS
                        if (mutable.value.role == RemoteRole.HOST) {
                            val old = mutable.value
                            val pendingExpired = old.hostEndpoint != null && now - pairingIssuedAt > QR_LIFETIME_MS
                            mutable.value = old.copy(
                                connected = online,
                                hostEndpoint = if (pendingExpired) null else old.hostEndpoint,
                                statusMessage = if (online) "Acesso remoto ativo" else if (active != null) "Controlador sem comunicação. O teste local continua." else if (pendingExpired) "QR expirado. Gere um novo código." else old.statusMessage,
                            )
                            if (!online) removeAccessNotification()
                        }
                    }
                }
            }
            try {
                while (isActive) {
                    val client = socket.accept()
                    if (!clientLimit.tryAcquire()) {
                        client.close()
                    } else {
                        scope.launch(Dispatchers.IO) {
                            try { handleClient(client) }
                            finally { clientLimit.release() }
                        }
                    }
                }
            } catch (_: CancellationException) {
            } catch (error: Exception) {
                if (isActive) mutable.value = mutable.value.copy(statusMessage = "Conexão encerrada: ${error.message}")
            } finally {
                heartbeatJob?.cancel()
                removeAccessNotification()
                runCatching { socket.close() }
                if (serverSocket === socket) serverSocket = null
                guard.withLock { grant = null; mutable.value = RemoteLabsUiState() }
            }
        }
    }

    /** Revogar uma sessão não finaliza o teste em andamento no aparelho monitorado. */
    fun revokeAccess() {
        scope.launch(Dispatchers.IO) {
            guard.withLock {
                grant = null
                sharedTestId = null
                pairingIssuedAt = 0L
                mutable.value = mutable.value.copy(
                    connected = false, connectedDevice = null, hostEndpoint = null,
                    actionMessage = "Acesso revogado. O teste local, se houver, continua normalmente.",
                    statusMessage = "Gere outro QR para autorizar novo aparelho.",
                )
                removeAccessNotification()
            }
        }
    }

    fun regenerateQr() {
        scope.launch(Dispatchers.IO) {
            guard.withLock {
                val socket = serverSocket ?: return@withLock
                val ip = socket.inetAddress.hostAddress?.takeIf(::isLocalIpv4) ?: return@withLock
                grant = null
                sharedTestId = null
                pairingIssuedAt = System.currentTimeMillis()
                mutable.value = mutable.value.copy(
                    hostEndpoint = newEndpoint(ip, socket.localPort),
                    connected = false, connectedDevice = null,
                    actionMessage = "Novo QR emitido. Autorizações anteriores foram revogadas.",
                    statusMessage = "Aguardando novo controlador.",
                )
                removeAccessNotification()
            }
        }
    }

    fun stopHosting() {
        hostJob?.cancel()
        heartbeatJob?.cancel()
        runCatching { serverSocket?.close() }
        removeAccessNotification()
        mutable.value = RemoteLabsUiState()
    }

    fun connect(payload: String) {
        val endpoint = RemoteEndpoint.fromPayload(payload)
        if (endpoint == null) {
            mutable.value = mutable.value.copy(statusMessage = "Código QR incompatível ou inválido. Atualize os dois aparelhos para a alpha.69.")
            return
        }
        pollJob?.cancel()
        val id = newSecret()
        val device = displayName(Build.MODEL ?: "Celular B")
        mutable.value = RemoteLabsUiState(
            role = RemoteRole.CONTROLLER, controllerEndpoint = endpoint,
            statusMessage = "Solicitando autorização a ${endpoint.deviceName}…",
        )
        pollJob = scope.launch(Dispatchers.IO) {
            val hello = request(endpoint, JSONObject().put("type", "pair").put("token", endpoint.token)
                .put("clientId", id).put("deviceName", device))
            val sessionSecret = hello?.optString("sessionSecret").orEmpty()
            if (hello?.optBoolean("ok") != true || !sessionSecret.matches(Regex("[0-9a-f]{32}"))) {
                mutable.value = mutable.value.copy(connected = false, statusMessage =
                    hello?.optString("message")?.takeIf { it.isNotBlank() } ?: "Não foi possível parear. Verifique a rede e gere outro QR.")
                return@launch
            }
            if (!isActive) return@launch
            val session = ControllerSession(endpoint, id, sessionSecret)
            controller = session
            while (isActive && controller === session) {
                val response = request(endpoint, signed("status", session))
                if (!isActive || controller !== session) break
                val online = response?.optBoolean("ok") == true
                val snapshot = if (online) runCatching {
                    RemoteSnapshot.fromJson(response!!.getJSONObject("snapshot"))
                }.getOrNull() else null
                mutable.value = mutable.value.copy(
                    connected = online && snapshot != null, snapshot = snapshot,
                    statusMessage = if (online && snapshot != null) "Conectado" else if (response?.optString("code") == "DENIED") "Acesso encerrado pelo celular monitorado." else "Conexão perdida. Tentando reconectar à sessão autorizada…",
                )
                if (response?.optString("code") == "DENIED") break
                delay(if (online) 1500L else 3000L)
            }
        }
    }

    fun disconnectController() {
        pollJob?.cancel()
        pollJob = null
        val session = controller
        controller = null
        mutable.value = RemoteLabsUiState()
        if (session != null) scope.launch(Dispatchers.IO) {
            request(session.endpoint, signed("disconnect", session))
        }
    }

    fun startRemoteLab(kind: String, sky: String, watts: Int?, minutes: Int?, stopAtFull: Boolean) {
        val session = controller ?: return
        if (kind !in setOf("SOLAR", "ELECTRIC")) return
        scope.launch(Dispatchers.IO) {
            val payload = signed("start", session).put("kind", kind).put("stopAtFull", stopAtFull)
            if (kind == "SOLAR") {
                payload.put("sky", sky)
                if (watts != null) payload.put("panelWatts", watts)
            }
            if (minutes != null) payload.put("minutes", minutes)
            val response = request(session.endpoint, payload)
            if (controller !== session) return@launch
            mutable.value = mutable.value.copy(actionMessage = if (response?.optBoolean("ok") == true)
                "Teste ${if (kind == "SOLAR") "solar" else "elétrico"} iniciado no outro aparelho."
                else response?.optString("message")?.takeIf { it.isNotBlank() } ?: "Não foi possível iniciar o teste remoto.")
        }
    }

    fun stopRemoteLab() {
        val session = controller ?: return
        scope.launch(Dispatchers.IO) {
            val response = request(session.endpoint, signed("stop", session))
            if (controller !== session) return@launch
            mutable.value = mutable.value.copy(actionMessage = if (response?.optBoolean("ok") == true)
                "Teste remoto finalizado. Relatório preservado no aparelho monitorado."
                else response?.optString("message")?.takeIf { it.isNotBlank() } ?: "Falha ao finalizar o teste remoto.")
        }
    }

    private fun signed(type: String, session: ControllerSession): JSONObject = JSONObject()
        .put("type", type).put("clientId", session.clientId).put("sessionSecret", session.sessionSecret)

    private suspend fun handleClient(socket: Socket) {
        socket.use { client ->
            if (!isLocalIpv4(client.inetAddress.hostAddress ?: "")) return
            client.soTimeout = 5000
            val line = runCatching { readShortLine(BufferedReader(InputStreamReader(client.getInputStream(), Charsets.UTF_8))) }.getOrNull() ?: return
            val request = runCatching { JSONObject(line) }.getOrNull() ?: return
            val response = guard.withLock {
                val type = request.optString("type")
                if (type == "pair") {
                    val code = mutable.value.hostEndpoint
                    val clientId = request.optString("clientId")
                    if (code == null || grant != null || System.currentTimeMillis() - pairingIssuedAt !in 0..QR_LIFETIME_MS ||
                        !sameSecret(code.token, request.optString("token")) || !clientId.matches(Regex("[0-9a-f]{32}"))) {
                        JSONObject().put("ok", false).put("message", "QR expirado ou já utilizado. Gere outro código.")
                    } else {
                        val display = displayName(request.optString("deviceName"))
                        val now = System.currentTimeMillis()
                        val credential = newSecret()
                        grant = RemoteGrant(clientId, credential, display, now, now)
                        sharedTestId = null
                        mutable.value = mutable.value.copy(hostEndpoint = null, connected = true, connectedDevice = display,
                            statusMessage = "Controlado por $display", actionMessage = "Novo aparelho pareado. Você pode revogar o acesso a qualquer momento.")
                        showAccessNotification(display)
                        JSONObject().put("ok", true).put("sessionSecret", credential)
                    }
                } else {
                    val previous = grant
                    if (previous == null || previous.clientId != request.optString("clientId") ||
                        !sameSecret(previous.sessionSecret, request.optString("sessionSecret"))) {
                        JSONObject().put("ok", false).put("code", "DENIED").put("message", "Acesso revogado ou não autorizado.")
                    } else {
                        val now = System.currentTimeMillis()
                        grant = previous.copy(lastSeenAt = now)
                        if (!mutable.value.connected) {
                            mutable.value = mutable.value.copy(connected = true, statusMessage = "Controlado por ${previous.deviceName}")
                            showAccessNotification(previous.deviceName)
                        }
                        when (type) {
                            "status" -> JSONObject().put("ok", true).put("snapshot", localSnapshot().toJson())
                            "disconnect" -> {
                                grant = null
                                mutable.value = mutable.value.copy(connected = false, connectedDevice = null,
                                    statusMessage = "O controlador desconectou. Gere outro QR para reconectar.")
                                removeAccessNotification()
                                JSONObject().put("ok", true)
                            }
                            "start" -> startRequest(request)
                            "stop" -> runCatching {
                                val active = labs.snapshot()
                                check(active != null) { "Nenhum teste em andamento." }
                                sharedTestId = active.id
                                labs.stop()
                                JSONObject().put("ok", true)
                            }.getOrElse { JSONObject().put("ok", false).put("message", it.message ?: "Falha ao finalizar.") }
                            else -> JSONObject().put("ok", false).put("message", "Comando remoto desconhecido.")
                        }
                    }
                }
            }
            val writer = OutputStreamWriter(client.getOutputStream(), Charsets.UTF_8)
            writer.write(response.toString()); writer.write("\n"); writer.flush()
        }
    }

    private suspend fun startRequest(request: JSONObject): JSONObject = runCatching {
        val kind = request.getString("kind")
        require(kind == "SOLAR" || kind == "ELECTRIC") { "Tipo de teste inválido." }
        val minutes = request.optInt("minutes").takeIf { request.has("minutes") }
        require(minutes == null || minutes in 1..1440) { "Duração inválida." }
        val stopAtFull = request.optBoolean("stopAtFull", true)
        val sky = request.optString("sky", "nublado")
        val watts = request.optInt("panelWatts").takeIf { request.has("panelWatts") }
        if (kind == "SOLAR") {
            require(sky in setOf("forte", "parcial", "nublado", "chuva"))
            require(watts != null && watts in 1..200)
        }
        /** Não começar foreground service silenciosamente por comando de rede em segundo plano. */
        check(repository.serviceActive.value) { "Ative o monitoramento no celular monitorado antes de iniciar remotamente." }
        if (kind == "SOLAR") labs.start(sky, watts, minutes, stopAtFull = stopAtFull)
        else labs.startElectric(minutes, stopAtFull = stopAtFull)
        sharedTestId = labs.snapshot()?.id
        JSONObject().put("ok", true)
    }.getOrElse { JSONObject().put("ok", false).put("message", it.message ?: "Falha ao iniciar teste.") }

    private suspend fun localSnapshot(): RemoteSnapshot {
        val now = System.currentTimeMillis()
        val reading = repository.reading.value.takeIf { it.updated in (now - 90_000L)..now }
        val active = labs.snapshot()
        if (active != null) sharedTestId = active.id
        val test: LabTest? = active ?: sharedTestId?.let { labs.report(it) }
        val testEnd = test?.endedAt ?: now
        val total = test?.let { (testEnd - it.startedAt).coerceAtLeast(0) } ?: 0L
        val charging = test?.chargingMs ?: 0L
        val noCharge = test?.notChargingMs ?: 0L
        return RemoteSnapshot(
            deviceName = displayName(Build.MODEL ?: "Celular monitorado"),
            batteryPercent = reading?.percent,
            currentMa = reading?.current,
            temperatureC = reading?.temperature,
            testElapsedMs = total,
            targetMs = test?.targetMs,
            startedAtMs = test?.startedAt,
            deadlineAtMs = test?.deadlineAt,
            endedAtMs = test?.endedAt,
            estimatedFullAtMs = active?.let { running -> LabTimeForecast.fullAt(
                running.kind, true, running.startedAt, running.lastReadingAt, now,
                running.initialPercent, running.finalPercent, running.chargingMs, running.lastMode) },
            testRunning = active != null,
            chargingMs = charging,
            notChargingMs = noCharge,
            unknownMs = LabMath.unknownMs(total, charging, noCharge),
            sky = test?.sky.orEmpty(),
            activeKind = test?.kind,
            activeId = test?.id,
            activeStatus = test?.status,
            initialPercent = test?.initialPercent,
            finalPercent = test?.finalPercent,
            initialTemperatureC = test?.initialTemperatureC,
            finalTemperatureC = test?.finalTemperatureC,
            meanTemperatureC = test?.let { t -> t.temperatureCount.takeIf { it > 0 }?.let { t.temperatureSumC / it } },
            maxTemperatureC = test?.maxTemperatureC,
            stopAtFull = test?.stopAtFull == true,
            electricSource = test?.electricSource?.takeIf { it.isNotBlank() },
        )
    }

    private suspend fun request(endpoint: RemoteEndpoint, data: JSONObject): JSONObject? = withContext(Dispatchers.IO) {
        runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(endpoint.host, endpoint.port), 3000)
                socket.soTimeout = if (data.optString("type") == "start") 35_000 else 4500
                val writer = OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8)
                writer.write(data.toString()); writer.write("\n"); writer.flush()
                val response = readShortLine(BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))) ?: return@use null
                JSONObject(response)
            }
        }.getOrNull()
    }

    private fun newEndpoint(ip: String, port: Int) = RemoteEndpoint(ip, port, newSecret(), displayName(Build.MODEL ?: "Celular A"))

    private fun showAccessNotification(name: String) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Acesso remoto Labs", NotificationManager.IMPORTANCE_LOW))
        val revokeIntent = Intent(context, RemoteRevokeReceiver::class.java).apply { action = RemoteRevokeReceiver.REVOKE }
        val revoke = PendingIntent.getBroadcast(context, 69, revokeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val open = PendingIntent.getActivity(context, 69, Intent(context, MainActivity::class.java).apply {
            putExtra("open_labs", true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Pulso Bateria: acesso remoto ativo")
            .setContentText("$name está monitorando este aparelho (nome informado pelo controlador).")
            .setContentIntent(open)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, "Encerrar acesso", revoke)
            .build()
        runCatching { manager.notify(NOTIFICATION_ID, notification) }
    }

    private fun removeAccessNotification() {
        runCatching { context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID) }
    }

    /** Host LAN apenas em IPv4 privado por Wi-Fi/hotspot, sem expor porta no modem celular. */
    private fun localIpv4Address(): String? = runCatching {
        Collections.list(NetworkInterface.getNetworkInterfaces()).asSequence()
            .filter { it.isUp && !it.isLoopback && (
                it.name.startsWith("wlan", true) || it.name.startsWith("ap", true) || it.name.startsWith("swlan", true)) }
            .flatMap { Collections.list(it.inetAddresses).asSequence() }
            .filterIsInstance<Inet4Address>().mapNotNull { it.hostAddress }.firstOrNull(::isLocalIpv4)
    }.getOrNull()
}

/** Limitar entrada antes de chamar JSONObject e sem permitir mensagens gigantes pela LAN. */
private fun readShortLine(reader: BufferedReader): String? {
    val builder = StringBuilder()
    while (builder.length < 4096) {
        val char = reader.read()
        if (char == -1) return null
        if (char == '\n'.code) return builder.toString()
        builder.append(char.toChar())
    }
    return null
}

private fun isLocalIpv4(host: String): Boolean {
    val segments = host.split('.')
    if (segments.size != 4) return false
    val parts = segments.map { value ->
        value.toIntOrNull()?.takeIf { it in 0..255 && (value == "0" || !value.startsWith('0')) } ?: return false
    }
    return parts[0] == 10 || (parts[0] == 172 && parts[1] in 16..31) || (parts[0] == 192 && parts[1] == 168)
}
