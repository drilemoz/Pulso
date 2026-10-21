#!/usr/bin/env python3
"""Remote Labs 2.1: contracts for top actions, encrypted recents and automatic charging.
These checks are static. Device UI, network, Keystore and Android lifecycle need physical tests.
"""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
vm=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
remote=(root/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text()
recent=(root/'app/src/main/java/br/com/pulsobateria/labs/RecentRemoteDevices.kt').read_text()
controller=(root/'app/src/main/java/br/com/pulsobateria/labs/LabController.kt').read_text()
full=(root/'core/src/main/kotlin/br/com/pulsobateria/core/LabFullCharge.kt').read_text()
auto=(root/'core/src/main/kotlin/br/com/pulsobateria/core/LabAutoStop.kt').read_text()
backup=(root/'app/src/main/java/br/com/pulsobateria/data/BackupManager.kt').read_text()
checks={
 'role actions at top, no forced modal': 'RemoteGatewayRow(' in ui and 'showRoleChoice' not in ui and 'Ser monitorado' in ui and 'Monitorar outro celular' in ui,
 'recent devices card and explicit forget': 'RecentRemoteDevicesCard(' in ui and 'pendingDelete?.let(onForget)' in ui and 'Esquecer aparelho?' in ui,
 'authorized session only': 'ControllerSession(endpoint,saved.clientId,saved.secret)' in remote and 'signed("status",session)' in remote and 'fun connectRecent(id: String)' in remote,
 'no reuse of QR token in history': 'RemoteEndpoint(saved.host,saved.port,"",' in remote and 'val credential = newSecret()' in remote,
 'stored session encrypted at rest': 'AndroidKeyStore' in recent and 'AES/GCM/NoPadding' in recent and 'GCMParameterSpec' in recent,
 'no insecure storage fallback': 'return emptyList()' in recent and 'private val prefs' in recent and 'Base64.encodeToString(cipher.iv+bytes' in recent,
 'limit history to 5': 'entries.take(5)' in recent and 'updated.take(5)' in recent,
 'history persisted only on accepted pairing': 'hello?.optBoolean("ok")' in remote and 'remembered.remember(endpoint.host' in remote,
 'host revocation still works': 'fun revokeAccess()' in remote and 'grant = null' in remote,
 'disconnect retains trusted host session': 'fun disconnectController()' in remote and 'mutable.value = RemoteLabsUiState(recentDevices=recents())' in remote,
 'viewmodel reconnect and forget': 'fun reconnectRecentRemote(' in vm and 'fun forgetRecentRemote(' in vm,
 'auto remote command no duration': 'onAuto={vm.startRemoteLab("AUTO","",null,null,true)}' in ui and 'else labs.startAuto()' in remote,
 'auto is neutral': '"AUTO" -> "✨ Carga automática"' in ui and '"AUTO" -> "Pulso Bateria • Carga Automática"' in ui,
 '100% mode unchanged plus AUTO': '"AUTO"' in full and 'percent == 100' in full,
 'auto has actual charging before disconnect end': 'previouslyCharged && plugged == false' in auto and 'MIN_DISCONNECTED_MS = 180_000L' in auto,
 'no missing-reading disconnect': 'MAX_SAMPLE_GAP_MS = 90_000L' in auto and 'reading.updated-oldWall <= LabAutoStop.MAX_SAMPLE_GAP_MS' in controller,
 'auto persisted using existing LabTest': 'startInternal("AUTO"' in controller and '"AUTO"' in backup and '"UNPLUGGED"' in backup,
 'detail hierarchy': 'Bateria atual' in ui and 'Ganho no teste' in ui and 'technicalExpanded' in ui,
 'no room change': 'version = 11' in (root/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text(),
}
for name,ok in checks.items(): print(('PASS' if ok else 'FAIL'),name)
assert all(checks.values()),'Remote recent / AUTO contract failure'
print(f'PASS: {len(checks)} contratos de Monitoramento remoto 2.1')
