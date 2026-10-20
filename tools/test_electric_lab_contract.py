#!/usr/bin/env python3
"""Contratos elétricos/temperatura e migração SQLite real. Não substitui APK compilado."""
from pathlib import Path
import sqlite3, re
r=Path(__file__).resolve().parents[1]
store=(r/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text()
controller=(r/'app/src/main/java/br/com/pulsobateria/labs/LabController.kt').read_text()
backup=(r/'app/src/main/java/br/com/pulsobateria/data/BackupManager.kt').read_text()
ui=(r/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
vm=(r/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
app=(r/'app/src/main/java/br/com/pulsobateria/PulsoApp.kt').read_text()
assert 'version = 10' in store and 'MIGRATION_9_10' in store
assert 'BatteryDatabase.MIGRATION_9_10' in app
assert 'const val VERSION = 8' in backup
assert '"ELECTRIC"' in backup and 'temperatureSumC' in backup and 'temperatureCount' in backup
assert 'temperatureSumC=if(backupVersion>=8)' in backup
assert 'temperatureCount=if(backupVersion>=8)' in backup
assert 'electricSource=if(backupVersion>=8)' in backup
assert 'fun startElectricLab(' in vm and 'startElectric(' in controller
assert 'reading?.plugged == true' in controller and 'reading.mode.name in setOf("CHARGING", "FULL")' in controller
assert 'LabTemperature.add(' in controller and 'LabTemperature.meanC(' in ui
assert '"electric" -> vm.startElectricLab(' in ui and '"ELECTRIC" -> "Pulso Bateria • Carregamento Elétrico"' in ui
assert 'Média calculada com ${test.temperatureCount} leituras válidas' in ui
assert 'if(!electric)' in ui and 'if(test.kind=="ELECTRIC")' in ui
assert 'freshEndTemp=recent?.takeIf { it.updated > (test.lastReadingAt ?: test.startedAt) }' in controller
instrumented=(r/'app/src/androidTest/java/br/com/pulsobateria/data/RoomMigrationTest.kt').read_text()
assert 'carregamentoEletricoPersisteTemperaturasEMedia' in instrumented
# Workflow 46: Kotlin rejected a LabTest fixture missing required targetMs/deadlineAt.
# Guard the exact electric fixture, not just presence of unrelated fields in the file.
fixture=instrumented.split('fun carregamentoEletricoPersisteTemperaturasEMedia()',1)[1].split('val stored=db.labs().byId(savedId)',1)[0]
assert re.search(r'\bLabTest\(',fixture)
assert re.search(r'\btargetMs\s*=\s*60_000L',fixture)
assert re.search(r'\bdeadlineAt\s*=\s*61_000L',fixture)
assert 'endedAt=61_000L' in fixture and 'status="COMPLETED"' in fixture
# Executa exatamente as três instruções SQL presentes na migração declarada, não uma reimplementação.
section=store.split('val MIGRATION_9_10 =',1)[1].split('val MIGRATION_8_9',1)[0]
sql=re.findall(r'db.execSQL\("([^"\n]+)"\)',section)
assert len(sql)==3,sql
with sqlite3.connect(':memory:') as db:
 db.execute('CREATE TABLE lab_tests(id INTEGER PRIMARY KEY,kind TEXT NOT NULL,status TEXT NOT NULL)')
 db.executemany('INSERT INTO lab_tests VALUES(?,?,?)',[(1,'SOLAR','STOPPED'),(2,'DISCHARGE','COMPLETED')])
 for command in sql: db.execute(command)
 values=db.execute('SELECT kind,temperatureSumC,temperatureCount,electricSource FROM lab_tests ORDER BY id').fetchall()
 assert values == [('SOLAR',0.,0,''),('DISCHARGE',0.,0,'')],values
 db.execute("INSERT INTO lab_tests(id,kind,status,temperatureSumC,temperatureCount,electricSource) VALUES(3,'ELECTRIC','COMPLETED',115.4,3,'Tomada')")
 assert db.execute('SELECT ROUND(temperatureSumC/temperatureCount,2) FROM lab_tests WHERE id=3').fetchone()[0]==38.47
 assert db.execute('PRAGMA table_info(lab_tests)').fetchall()
print('PASS: Labs elétrico, persistência térmica, backup JSON v8, migração 9→10 SQLite')
