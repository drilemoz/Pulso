#!/usr/bin/env python3
"""Static LAN boundary and remote screen contracts; physical Android tests remain mandatory."""
from pathlib import Path
import xml.etree.ElementTree as ET
root = Path(__file__).resolve().parents[1]
manager = (root/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text()
screen = (root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
app = (root/'app/src/main/java/br/com/pulsobateria/PulsoApp.kt').read_text()
vm = (root/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
gradle = (root/'app/build.gradle.kts').read_text()
manifest = ET.parse(root/'app/src/main/AndroidManifest.xml').getroot()
ns = '{http://schemas.android.com/apk/res/android}'
permissions = {x.attrib[ns+'name'] for x in manifest.findall('uses-permission')}
checks = {
 'required permissions only': {'android.permission.INTERNET','android.permission.CAMERA'} <= permissions and 'android.permission.WAKE_LOCK' not in permissions,
 'QR dependencies': 'com.journeyapps:zxing-android-embedded' in gradle and 'com.google.zxing:core' in gradle,
 'private IPv4 token validation': 'isLocalIpv4(host)' in manager and 'token.matches(Regex(' in manager,
 'server binds chosen Wi-Fi interface': 'ServerSocket(0, 16, InetAddress.getByName(ip))' in manager and 'it.name.startsWith("wlan"' in manager,
 'no public host accepted': 'a == 10 || (a == 172 && b in 16..31) || (a == 192 && b == 168)' in manager,
 'socket timeouts': 'InetSocketAddress(endpoint.host, endpoint.port), 3500' in manager and 'socket.soTimeout' in manager and 'client.soTimeout = 5000' in manager,
 'host revocation enforced': 'mutable.value.hostEndpoint?.token != endpoint.token' in manager,
 'host requested monitor while screen is visible': 'onMonitor(true); vm.startRemoteHosting()' in screen,
 'stale readings hidden': 'it.updated in (now-90_000L)..now' in manager,
 'remote commands gated by connection': 'OutlinedButton(enabled=connected' in screen,
 'remote method not shadowed by JSON variable': 'val response = request(endpoint, payload)' in manager and 'val request = JSONObject().put("type", "startSolar")' not in manager,
 'remote solar start and stop mapped': '"startSolar"' in manager and '"stopLab"' in manager and 'labs.start(' in manager and 'labs.stop()' in manager,
 'host and client connected to app': 'remoteLabs = RemoteLabsManager(this, labs, repository, scope)' in app and 'val remoteUi =' in vm,
 'designed two-card gateway': 'RemoteGatewayRow(' in screen and '"Celular A"' in screen and '"Celular B"' in screen,
 'no false demo-data notice': 'Prévia ilustrativa, não são leituras reais' not in screen,
}
for k,v in checks.items(): print(('PASS' if v else 'FAIL'), k)
assert all(checks.values()), 'Remote Labs static contract failed'
print(f'{len(checks)} LAN/QR contracts passed; actual Android and two-device tests pending.')
