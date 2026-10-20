#!/usr/bin/env python3
"""Static regression checks for session presentation. Not Android UI compilation."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoScreen.kt').read_text()
core=(root/'core/src/main/kotlin/br/com/pulsobateria/core/SessionHistoryText.kt').read_text()
checks={
    'history sees fresh reading': 'History(sessions,settings,reading,vm' in ui,
    'grouping before filter': ui.index('SessionHistoryText.groupConsecutive')<ui.index('val filteredGroups'),
    'opt-out toggle relocated to header': 'Switch(checked=groupNearby,onCheckedChange={groupNearby=it}' in ui and 'Unir ≤5 min' in ui,
    'original sessions accessible': 'members.forEach { session ->' in ui and 'SessionHistoryCard(session,settings,vm,possibleResumptions[session.id]' in ui,
    'sum only observed session duration': 'SessionArithmetic.group(members.map { it.toObservedPart() })' in ui and 'val duration=totals?.observedDurationMs ?: 0L' in ui,
    'gap shown separately': 'sem leitura (não contado)' in ui,
    'current requires fresh Android reading': 'screenNow-reading.updated !in 0L..180_000L' in ui and '!it.reason.startsWith("Pausada")' in ui,
    'current indicator available for originals and groups': 'CurrentSessionBadge(accent)' in ui and 'isCurrent=session.id==currentSessionId' in ui,
    'reasons restricted': 'previous.reason in setOf("Observação interrompida", "Intervalo sem observação", "Monitoramento pausado")' in core,
    'five minute visual cap': 'gap in 0L..300_000L' in core and 'acima de 5 minutos' in ui,
    'five options in one row': 'history-tools-one-row' in ui and 'listOf("Comparar","Relatórios","Todas","Carga","Descarga")' in ui,
    'history averages use only completed sessions': 'sessions.filter { it.closed }' in ui and 'DischargeForecast.history(completedSessions' in ui and 'ChargeForecast.history(completedSessions' in ui,
    'both source and name protected': 'previous.chargeSource == current.chargeSource' in core and 'previous.chargerLabel == current.chargerLabel' in core,
}
for label,passed in checks.items(): print(('PASS' if passed else 'FAIL')+': '+label)
assert all(checks.values()), 'Session grouping presentation regression'
print(f'{len(checks)}/{len(checks)} session grouping static checks passed')
