#!/usr/bin/env python3
"""Contracts for LAN v2, Compose dialog, authorization and battery data integrity.

Static tests do not replace real Android camera, Wi-Fi and permission tests.
"""
from pathlib import Path
import xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
manager=(root/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text()
screen=(root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
scanner=(root/'app/src/main/java/br/com/pulsobateria/ui/RemoteQrScannerDialog.kt').read_text()
receiver=(root/'app/src/main/java/br/com/pulsobateria/labs/RemoteRevokeReceiver.kt').read_text()
vm=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
app=(root/'app/src/main/java/br/com/pulsobateria/PulsoApp.kt').read_text()
gradle=(root/'app/build.gradle.kts').read_text()
manifest=ET.parse(root/'app/src/main/AndroidManifest.xml').getroot()
ns='{http://schemas.android.com/apk/res/android}'
permissions={p.attrib[ns+'name'] for p in manifest.findall('uses-permission')}
receivers={p.attrib[ns+'name']: p.attrib.get(ns+'exported') for p in manifest.findall('application/receiver')}
checks={
 'permissions narrow LAN+QR': {'android.permission.INTERNET','android.permission.CAMERA'} <= permissions and 'android.permission.WAKE_LOCK' not in permissions,
 'existing QR libraries reused': 'com.journeyapps:zxing-android-embedded:4.3.0' in gradle and 'com.google.zxing:core:3.5.3' in gradle,
 'dialog scanner, not CaptureActivity': 'RemoteQrScannerDialog(' in screen and 'AndroidView(factory = { view }' in scanner and 'ScanContract' not in screen,
 'scanner lifecycle cleanup': 'view.resume()' in scanner and 'view.pause()' in scanner and 'onDispose' in scanner,
 'scanner portrait and optional camera': 'SCREEN_ORIENTATION_PORTRAIT' in scanner and 'RequestPermission()' in scanner and 'setUseTextureView(true)' in scanner,
 'role chooser': 'RemoteRoleChoiceDialog(' in screen and 'Ser monitorado' in screen and 'Monitorar outro celular' in screen,
 'switch role confirmation': 'Trocar a função deste aparelho?' in screen and 'confirmSwitchRole' in screen,
 'QR v2 and private IPv4 restriction': 'REMOTE_VERSION = 2' in manager and 'isLocalIpv4(host)' in manager and 'InetAddress.getByName(ip)' in manager,
 'host Wi-Fi/hotspot filter': 'it.name.startsWith("wlan"' in manager and 'it.name.startsWith("ap"' in manager,
 'pair token single-use': 'grant != null' in manager and 'pairingIssuedAt' in manager and 'QR_LIFETIME_MS = 300_000L' in manager,
 'credential distinct from QR': 'RemoteGrant(' in manager and 'val credential = newSecret()' in manager and 'sessionSecret' in manager,
 'only one authorized controller': 'grant: RemoteGrant?' in manager and 'previous.clientId' in manager,
 'explicit revocation': 'fun revokeAccess()' in manager and 'grant = null' in manager and 'regenerateQr()' in manager,
 'notification and private revoke action': 'showAccessNotification(' in manager and 'removeAccessNotification()' in manager and receivers.get('.labs.RemoteRevokeReceiver') == 'false' and 'REVOKE' in receiver and 'Manifest.permission.POST_NOTIFICATIONS' in manager,
 'name displayed not verified identity': 'connectedDevice' in manager and 'Acessado por:' in screen and 'não representa identidade certificada' in screen,
 'no silent remote FGS start': 'check(repository.serviceActive.value)' in manager and 'startForegroundService' not in manager,
 'bounded sockets': 'client.soTimeout = 5000' in manager and 'readShortLine(' in manager and 'Socket().use' in manager,
 'stale Android readings discarded': 'it.updated in (now - 90_000L)..now' in manager,
 'Solar + Electric controller commands': 'kind == "SOLAR"' in manager and 'kind == "ELECTRIC"' in manager and 'labs.startElectric(' in manager and 'labs.start(' in manager,
 'stop on 100 option forwarded': 'stopAtFull = stopAtFull' in manager and 'onStopAtFull' in screen,
 'persisted lab report reused': 'labs.report(it)' in manager and 'LabTemperature' not in manager and 'temperatureSumC' in manager,
 'remotely observed values not demo': 'Prévia ilustrativa, não são leituras reais' not in screen and 'Sem conexão. Leituras indisponíveis' not in screen,
 'viewmodel and app integration': 'remoteLabs = RemoteLabsManager(this, labs, repository, scope)' in app and 'fun startRemoteLab(' in vm,
 'two-device physical tests documented': 'comunicação real entre dois celulares' in (root/'docs/VALIDATION.md').read_text(),
}
for label,passed in checks.items(): print(('PASS' if passed else 'FAIL'),label)
assert all(checks.values()), 'LAN v2 static contract failed'
print(f'{len(checks)} Remote Labs v2 static checks passed (physical tests still pending)')
