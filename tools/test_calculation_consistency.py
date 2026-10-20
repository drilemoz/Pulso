#!/usr/bin/env python3
"""Guard Home/Session arithmetic wiring. Actual Kotlin arithmetic is tested by :core:test.
Static contracts are NOT a substitute for compiling/running the Android application.
"""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoScreen.kt').read_text()
repo=(root/'app/src/main/java/br/com/pulsobateria/monitor/BatteryRepository.kt').read_text()
charge=(root/'core/src/main/kotlin/br/com/pulsobateria/core/ChargeForecast.kt').read_text()
math=(root/'core/src/main/kotlin/br/com/pulsobateria/core/SessionArithmetic.kt').read_text()
checks={
 'Home counts only finalized sessions': 'val completedSessions = remember(sessions) { sessions.filter { it.closed } }' in ui and
  'DischargeForecast.history(completedSessions.map' in ui and 'ChargeForecast.history(completedSessions.map' in ui,
 'History averages count only finalized sessions': 'val completedSessions = remember(sessions) { sessions.filter { it.closed } }' in ui[ui.index('private fun History('):] and
  'DischargeForecast.history(completedSessions.map' in ui[ui.index('private fun History('):],
 'State transition resets interval clock': 'lastElapsed=0L' in repo[repo.index('if (!paused && lastElapsed > 0 && BatteryMath.startsNewSession'):repo.index('if (level == null || mode == Mode.UNKNOWN)')],
 'No artificial gap in grouped rate': 'SessionArithmetic.group(members.map { it.toObservedPart() })' in ui and
  'val duration=totals?.observedDurationMs ?: 0L' in ui and 'val gap=totals?.unobservedBetweenMs ?: 0L' in ui,
 'Bad groups render original sessions': 'if(members.size>1 && SessionArithmetic.group(members.map { it.toObservedPart() })==null)' in ui,
 'Group sums only observed parts': 'observed += current.observedDurationMs' in math and
  'unobserved += gap' in math and 'BatteryMath.sessionRate(' in math,
 'Source count matches source window': 'val sourceSessionsUsed = if (sourceAverage==null) 0 else' in charge and
  'matchingRecent.size else matching.size' in charge,
 'Core arithmetic regressions present': (root/'core/src/test/kotlin/br/com/pulsobateria/core/SessionArithmeticTest.kt').exists(),
}
for label,passed in checks.items(): print(('PASS' if passed else 'FAIL')+': '+label)
assert all(checks.values()), 'Home/Sessions numerical consistency regression'
print(f'{len(checks)} calculation wiring checks passed')
