package br.com.pulsobateria.ui

import android.Manifest
import android.app.Activity
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.ResultPoint
import br.com.pulsobateria.labs.RemoteEndpoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

/** Scanner incorporado ao diálogo Compose, sem abrir a CaptureActivity horizontal. */
@Composable
internal fun RemoteQrScannerDialog(onScanned: (String) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var permitted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) }
    var torch by remember { mutableStateOf(false) }
    var delivered by remember { mutableStateOf(false) }
    var scanHint by remember { mutableStateOf("") }
    val latestScanned by rememberUpdatedState(onScanned)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permitted = it
    }
    val view = remember(context) { DecoratedBarcodeView(context).apply {
        barcodeView.setUseTextureView(true)
        setStatusText("Posicione o QR Code dentro do quadro")
    } }

    /** O diálogo herda a janela principal: forçar retrato só enquanto está aberto. */
    DisposableEffect(context) {
        val activity = generateSequence(context as android.content.Context?) { (it as? ContextWrapper)?.baseContext }
            .filterIsInstance<Activity>().firstOrNull()
        val original = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { if (original != null && activity != null) activity.requestedOrientation = original }
    }
    DisposableEffect(view, permitted, lifecycleOwner) {
        if (permitted) {
            view.decodeContinuous(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult?) {
                    val code = result?.text.orEmpty()
                    if (delivered || code.isBlank()) return
                    if (RemoteEndpoint.fromPayload(code) == null) {
                        scanHint = "QR incompatível. Use o Pulso Bateria alpha.69 nos dois aparelhos."
                        return
                    }
                    delivered = true
                    view.pause()
                    latestScanned(code)
                }
                override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) = Unit
            })
            val lifecycle = lifecycleOwner.lifecycle
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> view.resume()
                    Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> view.pause()
                    else -> Unit
                }
            }
            lifecycle.addObserver(observer)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) view.resume()
            onDispose { lifecycle.removeObserver(observer); view.pause() }
        } else onDispose { view.pause() }
    }
    LaunchedEffect(torch, permitted) {
        if (permitted) {
            if (torch) view.setTorchOn() else view.setTorchOff()
        }
    }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.92f), shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Escanear QR Code", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onClose) { Text("Fechar") }
                }
                Text("Aponte a câmera para o código mostrado no celular que será monitorado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (permitted) {
                    AndroidView(factory = { view }, modifier = Modifier.fillMaxWidth().height(290.dp))
                    if (scanHint.isNotBlank()) Text(scanHint, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = { torch = !torch }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (torch) "Desligar lanterna" else "Ligar lanterna")
                    }
                } else {
                    Text("A câmera precisa de autorização apenas para ler o QR Code. O restante do aplicativo funciona sem ela.",
                        style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.fillMaxWidth()) { Text("Permitir câmera") }
                }
                Text("O pareamento funciona somente na mesma rede Wi-Fi ou hotspot, sem internet.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
