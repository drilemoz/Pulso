from pathlib import Path
s=(Path(__file__).resolve().parents[1]/'app/src/main/java/br/com/pulsobateria/ui/LabsScreen.kt').read_text()
assert 'SolarDurationBar("A",a)' in s and 'SolarDurationBar("B",b)' in s
assert 'val unknown=(total-charging-noCharge).coerceAtLeast(0L)' in s
assert 'solarComparisonCsv(a,b)' in s and 'Copiar comparação CSV' in s
assert 'inicio_epoch_ms;fim_epoch_ms' in s
print('PASS: contratos estáticos de comparação Labs')
