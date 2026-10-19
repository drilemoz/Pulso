#!/usr/bin/env python3
"""Static regression guards for the two Compose errors from workflow 35.
Not a substitute for the Kotlin compiler.
"""
from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
source = (root / 'app/src/main/java/br/com/pulsobateria/ui/PulsoScreen.kt').read_text()
assert re.search(r'@Composable\s+private\s+fun\s+SmallInfoChip\s*\(', source), 'SmallInfoChip needs @Composable'
assert re.search(r'(?<!@Composable\n)private\s+data\s+class\s+ThermalUi\s*\(', source), 'ThermalUi must not be @Composable'
assert re.search(r'@Composable\s+private\s+fun\s+rememberThermalUi\s*\(', source), 'rememberThermalUi needs @Composable'
print('PASS: Compose regression guards for ThermalUi, rememberThermalUi and SmallInfoChip')
