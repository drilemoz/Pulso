#!/usr/bin/env python3
from pathlib import Path
s=(Path(__file__).resolve().parents[1]/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
assert 'vm.labCurrent.collectAsStateWithLifecycle()' in s
assert 'vm.labHistory.collectAsStateWithLifecycle()' in s
assert 'repeatOnLifecycle(Lifecycle.State.STARTED)' in s
assert 'while(true) { now=System.currentTimeMillis(); delay(1000L) }' in s
print('PASS: Labs lifecycle static contract')
