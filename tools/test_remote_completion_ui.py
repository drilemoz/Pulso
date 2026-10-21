#!/usr/bin/env python3
"""Contratos alpha.73: não inventar precisão, evitar notificação duplicada, finalizar e salvar.
Não substitui build Android ou teste entre dois celulares.
"""
from pathlib import Path
r=Path(__file__).resolve().parents[1]
ui=(r/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
manager=(r/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text()
completion=(r/'core/src/main/kotlin/br/com/pulsobateria/core/RemoteChargeCompletion.kt').read_text()
tests=(r/'core/src/test/kotlin/br/com/pulsobateria/core/RemoteChargeCompletionTest.kt').read_text()
lab=(r/'app/src/main/java/br/com/pulsobateria/labs/LabController.kt').read_text()
full=(r/'core/src/main/kotlin/br/com/pulsobateria/core/LabFullCharge.kt').read_text()
checks={
 'same height for current and temperature': 'modifier=modifier.height(132.dp)' in ui and ui.count('RemoteMetricCard(')==3,
 'units on same row': 'Row(verticalAlignment=Alignment.Bottom' in ui and 'unit="mA"' in ui and 'unit="°C"' in ui,
 'gain without p.p.': 'snapshot.finalPercent-snapshot.initialPercent}%' in ui and 'snapshot.finalPercent-snapshot.initialPercent} p.p.' not in ui,
 'context for AUTO': 'if(snapshot?.activeKind=="AUTO") Text("Carga automática"' in ui,
 'percent remains integer': 'val initialPercent:' in (r/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text(),
 'notification permission controller request': 'notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)' in ui and 'remoteUi.role==RemoteRole.CONTROLLER' in ui,
 'notification is on the controller': 'alertIfRemoteChargeFinished(session, snapshot)' in manager and manager.count('alertIfRemoteChargeFinished(session, snapshot)')==2,
 'completion notification channel': 'COMPLETION_CHANNEL_ID' in manager and 'NotificationManager.IMPORTANCE_DEFAULT' in manager,
 'notification opens Labs': 'putExtra("open_labs", true)' in manager and '.setAutoCancel(true)' in manager,
 'distinct from host access notification': 'showAccessNotification(' in manager and 'showCompletionNotification(' in manager,
 'one-time state transition': 'key !in active || key in notified' in completion and 'active.add(key)' in completion and 'notified.add(key)' in completion,
 'only charging sessions': 'kind !in setOf("SOLAR", "ELECTRIC", "AUTO")' in completion,
 'no false historical notification': 'assertNull(tracker.observe("celularA",23,"AUTO",false,"FULL"))' in tests,
 'polling repeated without extra notification': 'assertNull(tracker.observe("celularA",23,"AUTO",false,"FULL"))' in tests,
 'new Android 100 percent is observed': 'percent == 100' in full and 'sampleTimeMs > testStartedAtMs' in full,
 'persist before finishing': lab.index('dao.update(test)') < lab.index('finishLocked(test, reading.updated, "FULL")'),
 'completion persisted before local notification': lab.index('dao.update(saved)') < lab.index('if (status in setOf("COMPLETED","FULL","UNPLUGGED")) notifyFinished(saved)'),
 'Room unchanged': 'version = 11' in (r/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text(),
 'backup unchanged': 'const val VERSION = 9' in (r/'app/src/main/java/br/com/pulsobateria/data/BackupManager.kt').read_text(),
}
for label, passed in checks.items(): print(('PASS' if passed else 'FAIL')+': '+label)
assert all(checks.values()),'Remote completion / UI alpha.73 regressions failed'
print(f'PASS: {len(checks)} contratos de conclusão remota e layout')
