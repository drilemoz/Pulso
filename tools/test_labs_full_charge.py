#!/usr/bin/env python3
"""Contrato Labs 100% + SQL real da migração 10->11 e seus defaults conservadores."""
from pathlib import Path
import sqlite3, re
root=Path(__file__).resolve().parents[1]
store=(root/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text()
controller=(root/'app/src/main/java/br/com/pulsobateria/labs/LabController.kt').read_text()
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
vm=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
backup=(root/'app/src/main/java/br/com/pulsobateria/data/BackupManager.kt').read_text()
news=(root/'app/src/main/java/br/com/pulsobateria/ui/ReleaseNews.kt').read_text()
instrumented=(root/'app/src/androidTest/java/br/com/pulsobateria/data/RoomMigrationTest.kt').read_text()
core=(root/'core/src/main/kotlin/br/com/pulsobateria/core/LabFullCharge.kt').read_text()
assert 'version = 11' in store and 'MIGRATION_10_11' in store
assert '@ColumnInfo(defaultValue = "0") val stopAtFull: Boolean = false' in store
assert 'fun shouldStop(' in core and 'percent == 100' in core and 'kind in setOf("SOLAR", "ELECTRIC")' in core
assert 'reading.updated <= (test.lastReadingAt ?: 0L)' in controller
assert 'reading.updated <= test.startedAt' in controller
assert 'reading.updated >= test.deadlineAt' in controller
assert 'finishLocked(test, reading.updated, "FULL")' in controller
assert 'status=="FULL"' in controller and 'notifyFinished(saved)' in controller
assert 'if (status=="COMPLETED" || status=="FULL") notifyFinished(saved)' in controller
assert 'stopAtFull=stopAtFull && kind in setOf("SOLAR", "ELECTRIC")' in controller
assert 'notes,stopAtFull) { onMonitor(true) }' in ui and ui.count('notes,stopAtFull) { onMonitor(true) }')==2
assert 'if(solar || electric) item' in ui and 'Finalizar ao atingir 100%' in ui
assert 'var stopAtFull by rememberSaveable { mutableStateOf(true) }' in ui
assert 'test.status=="FULL"' in ui and 'Encerramento:' in ui
assert 'app.labs.startElectric(minutes,screen,camera,wifi,recording,notes,stopAtFull)' in vm
assert 'app.labs.start(sky,watts,minutes,screen,camera,wifi,recording,notes,stopAtFull)' in vm
assert 'const val VERSION = 9' in backup and 'put("stopAtFull",t.stopAtFull)' in backup
assert 'backupVersion>=9 && t.getBoolean("stopAtFull")' in backup
assert 't.status in setOf("STOPPED","COMPLETED","FULL")' in backup
assert 'migracaoDaVersao10PreservaOpcaoDesativadaEmTestesLegados' in instrumented
assert 'assertFalse(old.stopAtFull)' in instrumented and 'assertTrue(stored.stopAtFull)' in instrumented
assert '0.1.0-alpha.67' in news
section=store.split('val MIGRATION_10_11 =',1)[1].split('val MIGRATION_9_10',1)[0]
sql=re.findall(r'db.execSQL\("([^"\n]+)"\)',section)
assert len(sql)==1,sql
with sqlite3.connect(':memory:') as db:
 db.execute('CREATE TABLE lab_tests (id INTEGER PRIMARY KEY, kind TEXT NOT NULL, status TEXT NOT NULL)')
 db.executemany('INSERT INTO lab_tests VALUES (?,?,?)',[(1,'SOLAR','RUNNING'),(2,'ELECTRIC','COMPLETED'),(3,'DISCHARGE','STOPPED')])
 db.execute(sql[0])
 assert db.execute('SELECT stopAtFull FROM lab_tests ORDER BY id').fetchall()==[(0,),(0,),(0,)]
 db.execute('UPDATE lab_tests SET stopAtFull=1 WHERE id=1')
 assert db.execute('SELECT stopAtFull FROM lab_tests ORDER BY id').fetchall()==[(1,),(0,),(0,)]
 print('PASS: migração 10->11 preserva opção desligada no legado, sem sobrescrever dados')
print('PASS: encerramento ao atingir 100%, UI, relatório, backup e regra de leitura nova')
