#!/usr/bin/env python3
from pathlib import Path
root=Path(__file__).resolve().parents[1]
labs=(root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
assert 'val completed=remember(history)' in labs
assert 'val solarCompleted=remember(completed)' in labs
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/PulsoScreen.kt').read_text()
if 'alpha.52' in (root/'app/build.gradle.kts').read_text():
 assert 'remember(filteredGroups,locale)' in ui
 assert 'remember(dayKey,locale)' in ui
print('PASS: UI cache contracts')
