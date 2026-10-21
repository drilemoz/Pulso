#!/usr/bin/env python3
"""Alpha.72: testes sem prazo obrigatório e ETA em destaque sem inventar horários.

Contracts of Kotlin/Compose wiring; doesn't replace instrumented device tests.
"""
from pathlib import Path
r=Path(__file__).resolve().parents[1]
ui=(r/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
vm=(r/'app/src/main/java/br/com/pulsobateria/ui/PulsoViewModel.kt').read_text()
core=(r/'core/src/main/kotlin/br/com/pulsobateria/core/LabTimeForecast.kt').read_text()
checks={
 'no local duration picker': '"Duração do teste"' not in ui and 'selectedMinutes' not in ui and 'customMinutes' not in ui,
 'Solar indefinite': 'vm.startLab(sky,selectedWatts,null,' in ui,
 'Electric indefinite': 'vm.startElectricLab(null,screenCondition,' in ui,
 'Discharge indefinite': 'vm.startDischargeLab(null,screenCondition,' in ui,
 'remote duration removed': 'remoteDuration' not in ui and 'remoteCustomMinutes' not in ui,
 'remote start indefinite': 'vm.startRemoteLab(remoteKind,remoteSky,watts,null,' in ui,
 'remote AUTO independent': 'onAuto={vm.startRemoteLab("AUTO","",null,null,true)}' in ui,
 'no Room schema changes': 'version = 11' in (r/'app/src/main/java/br/com/pulsobateria/data/Store.kt').read_text(),
 'large ETA card local': 'private fun LabFinishForecastCard(' in ui and 'LabFinishForecastCard(test.kind,true,test.stopAtFull,fullPrediction,now)' in ui,
 'large ETA in primary remote card': '"Término previsto do teste"' in ui and 'style=if(predicted!=null) MaterialTheme.typography.headlineLarge' in ui,
 'remaining time shown beside predicted clock': 'estimatedRemainingLabel(target,nowMs)' in ui and 'estimatedRemainingLabel(target,System.currentTimeMillis())' in ui,
 'no fabricated time for discharge': '"Quando você finalizar"' in ui and 'kind in setOf("SOLAR", "ELECTRIC", "AUTO")' in ui,
 'insufficient readings explicit': '"Aguardando medições"' in ui and 'gained < 2' in core and 'elapsed < MIN_SPAN_MS' in core,
 'legacy deadlines remain visible': 'if(deadlineAtMs!=null) Row' in ui and '"Término programado"' in ui,
 'real duration and reports preserved': 'appendLine("Duração:' in ui and 'appendLine("Fim efetivo:' in ui,
 '100% rule preserved': 'notes,stopAtFull)' in ui and 'if(remoteKind=="AUTO") true else remoteStopAtFull' in ui,
}
for name,ok in checks.items():
 print(('PASS' if ok else 'FAIL') + ': ' + name)
assert all(checks.values()), 'Contratos alpha.72 falharam'
print('PASS: %d contratos Labs sem prazo + ETA em destaque' % len(checks))
