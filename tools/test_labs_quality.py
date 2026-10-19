#!/usr/bin/env python3
"""Contract checks only; Kotlin/JUnit tests run in the Android CI build."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ui=(root/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
core=(root/'core/src/main/kotlin/br/com/pulsobateria/core/LabComparisonQuality.kt').read_text()
tests=(root/'core/src/test/kotlin/br/com/pulsobateria/core/LabComparisonQualityTest.kt').read_text()
assert 'LabComparisonQuality.warnings(summary(a),summary(b))' in ui
assert 'Limitações da comparação' in ui
assert 'coveragePercent' in core and 'max / min > 1.25' in core
assert tests.count('@Test')>=3
print('PASS: contrato estático de qualidade da comparação solar')
