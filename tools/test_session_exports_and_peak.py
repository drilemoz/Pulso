#!/usr/bin/env python3
"""Gate de contratos de novos recursos alpha.60; não substitui o Android compiler."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoScreen.kt').read_text()
vm=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
store=(root/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text()
repo=(root/'app/src/main/java/br/com/pulsobateria/monitor/BatteryRepository.kt').read_text()
backup=(root/'app/src/main/java/br/com/pulsobateria/data/BackupManager.kt').read_text()
assert 'maxTemperatureC: Double? = null' in store
assert 'SessionThermalPeak.update(previous.maxTemperatureC,temperature)' in repo
assert 'MIGRATION_8_9' in store and 'version = 9' in store
assert 'maxTemperatureC' in backup and 'const val VERSION = 7' in backup
assert 's.maxTemperatureC ?: retainedPeaks[s.id]' in vm and 'origem_pico' in vm
assert 'suspend fun exportRetainedTemperaturePeaks()' in store
application=(root/'app/src/main/java/br/com/pulsobateria/PulsoApp.kt').read_text()
assert 'lateinit var database: BatteryDatabase; private set' in application, 'Export viewmodel uses app.database'
assert 'database = Room.databaseBuilder(' in application, 'Database initialized before ViewModel' 
assert 'Dispatchers.IO' in ui and 'bitmap.recycle()' in ui
assert 'PrintCameraIcon()' in ui and 'Icons.Default.CameraAlt' not in ui and 'Icons.Default.FileDownload' not in ui
assert 'ActivityResultContracts.CreateDocument("text/csv")' in ui
assert 'ActivityResultContracts.CreateDocument("image/png")' in ui
assert 'SessionDetailRow("Temperatura máxima registrada"' in ui
print('PASS: Pico persistente, Room/backup, CSV, captura segura e ícone local')

# Alpha.61: regressions covering Android migration fixture and UI concurrency.
migration=(root/'app/src/androidTest/java/br/com/pulsobateria/data/RoomMigrationTest.kt').read_text()
assert 'ADD COLUMN `$"+it.substringBefore' not in migration, 'A fixture criava colunas com prefixo $ em vez das colunas reais'
assert 'ADD COLUMN `$column` $sqlType' in migration
assert 'maximaTermicaPersistenteEmCargaEDescargaSemAmostraBruta' in migration
assert 'for(mode in listOf("CHARGING","DISCHARGING"))' in migration
assert 'screenshotBusy=false' in ui and 'enabled=!screenshotBusy' in ui
assert 'enabled=!vm.sessionExportBusy' in ui and 'sessionExportBusy' in vm
assert 'inicio_local' in vm and 'fim_ou_ultima_leitura_local' in vm and 'DateTimeFormatter' in vm
print('PASS: Contratos alpha61 de migração Room, pico térmico, capturas e CSV legível')
