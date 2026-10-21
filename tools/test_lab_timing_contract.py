#!/usr/bin/env python3
"""Guardas estáticas dos horários local/remoto. Testes Kotlin validam a matemática."""
from pathlib import Path
root = Path(__file__).resolve().parents[1]
ui = (root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
remote = (root/'app/src/main/java/br/com/pulsobateria/labs/RemoteLabsManager.kt').read_text()
core = (root/'core/src/main/kotlin/br/com/pulsobateria/core/LabTimeForecast.kt').read_text()
checks = {
 'nova função matemática isolada': 'object LabTimeForecast' in core and 'fun fullAt(' in core,
 'não confunde prazo com previsão': 'Não é o prazo do teste.' in ui,
 'previsão apenas para carga': 'kind !in setOf("SOLAR", "ELECTRIC", "AUTO")' in core,
 'sem falsa previsão sem dados': 'gained < 2' in core and 'elapsed < MIN_SPAN_MS' in core and 'return null' in core,
 'recusa leituras antigas': 'MAX_READING_AGE_MS' in core and 'nowMs-lastReadingAtMs' in core,
 'exige carga confirmada': 'chargingMs.toDouble()/elapsed < 0.75' in core and 'lastMode != "CHARGING"' in core,
 'trata relógio civil inválido': 'Long.MAX_VALUE-remaining.toLong()' in core,
 'mostra relógio na tela local': 'LabTimeSummary(test.startedAt, test.deadlineAt' in ui,
 'mostra relógio no painel remoto': 'LabTimeSummary(' in ui and 'snapshot.startedAtMs?.let' in ui,
 'mostra hora de início e término': '"Término programado"' in ui and '"Início"' in ui,
 'mostra tempo decorrido/restante': '"Tempo decorrido"' in ui and '"Tempo restante"' in ui,
 'mostra ETA 100% distinto': '"100% previsto:' in ui and 'estimatedFullLabel(' in ui,
 'sinaliza testes manuais': 'Manual (sem prazo)' in ui and 'Teste manual: sem horário fixo' in ui,
 'mostra prazo no relatório copiado': 'appendLine("Término programado:' in ui,
 'mostra prazo no histórico expandido': 'LabResultRow("Término programado"' in ui,
 'transmite timestamps reais': all(f'val {name}: Long? = null' in remote for name in ('startedAtMs','deadlineAtMs','endedAtMs','estimatedFullAtMs')),
 'decodifica timestamps e não os infere': all(f'j.optLong("{name}")' in remote for name in ('startedAtMs','deadlineAtMs','endedAtMs','estimatedFullAtMs')),
 'serializa timestamps LAN': all(f'put("{name}", {name})' in remote for name in ('startedAtMs','deadlineAtMs','endedAtMs','estimatedFullAtMs')),
 'calcula ETA no telefone monitorado': 'estimatedFullAtMs = active?.let' in remote and 'LabTimeForecast.fullAt(' in remote,
 'sem modificar Room': 'version = 11' in (root/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text(),
}
for name, result in checks.items():
 print(('PASS' if result else 'FAIL'), name)
assert all(checks.values()), 'Falha no contrato de horários Labs'
print(f'{len(checks)} regras dos horários Labs verificadas')
