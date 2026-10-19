#!/usr/bin/env python3
"""Auditoria independente de consistência de release; não requer Android SDK."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
gradle=(root/'app/build.gradle.kts').read_text()
name=re.search(r'versionName\s*=\s*"([^"]+)"',gradle).group(1)
code=int(re.search(r'versionCode\s*=\s*(\d+)',gradle).group(1))
assert name==f'0.1.0-alpha.{code}',(name,code)
change=(root/'CHANGELOG.md').read_text()
heads=re.findall(r'^## 0\.1\.0-alpha\.(\d+) \(',change,re.M)
assert heads and int(heads[0])==code,'CHANGELOG latest version mismatch'
assert len(heads)==len(set(heads)),'Duplicate CHANGELOG release heading'
assert all(int(a)>int(b) for a,b in zip(heads,heads[1:])),'CHANGELOG order invalid'
workflow=(root/'.github/workflows/android.yml').read_text()
for target in (f'Pulso-Bateria-v{name}-debug.apk',f'apk-alpha.{code}-'):
 assert target in workflow,target
for path in ('README.md','RELEASE_NOTES.md','docs/VERSIONING.md','docs/VALIDATION.md'):
 assert name in (root/path).read_text(),path
agents=(root/'AGENTS.md').read_text()
assert f'Próxima 0.1.0-alpha.{code+1} / {code+1}.' in agents
print(f'PASS: release alpha.{code}; {len(heads)} entradas únicas e ordenadas; workflow e documentação sincronizados')
