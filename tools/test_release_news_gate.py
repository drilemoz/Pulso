#!/usr/bin/env python3
"""Regression tests for the release-news build gate."""
import tempfile, pathlib, shutil, runpy, contextlib, io
root = pathlib.Path(__file__).resolve().parents[1]
paths = ['app/build.gradle.kts','app/src/main/java/br/com/pulsobateria/ui/ReleaseNews.kt','CHANGELOG.md','RELEASE_NOTES.md','tools/check_release_news.py']
def run(changes=None):
 with tempfile.TemporaryDirectory() as tmp:
  d=pathlib.Path(tmp)
  for rel in paths:
   (d/rel).parent.mkdir(parents=True,exist_ok=True)
   shutil.copy2(root/rel,d/rel)
  for rel, old, new in (changes or []):
   p=d/rel
   text=p.read_text(); assert old in text, (rel,old)
   p.write_text(text.replace(old,new,1))
  try:
   with contextlib.redirect_stdout(io.StringIO()): runpy.run_path(str(d/'tools/check_release_news.py'))
   return 0
  except AssertionError:
   return 1
news='app/src/main/java/br/com/pulsobateria/ui/ReleaseNews.kt'
assert run()==0, 'Current build must pass'
# Obter versionamento e novidades do build real; os testes não podem ficar presos à alpha anterior.
import re
gradle_text=(root/'app/build.gradle.kts').read_text()
version=int(re.search(r'versionCode\s*=\s*(\d+)',gradle_text).group(1))
current_news=(root/news).read_text()
entries=re.findall(r'^\s*"([^"\n]+)",?$',current_news,re.M)
assert len(entries)>=2
first,second=entries[:2]
header=f'## 0.1.0-alpha.{version}'
previous=f'## 0.1.0-alpha.{version-1}'
assert run([(news,f'const val VERSION_CODE = {version}',f'const val VERSION_CODE = {version-1}')])!=0
assert run([(news,'"'+first+'",','"",')])!=0
assert run([(news,'"'+second+'",','"'+first+'",')])!=0
assert run([('CHANGELOG.md',header,previous)])!=0
assert run([(news,first,'Texto ausente do changelog')])!=0
# A data das releases é informativa e pode variar; capturar o cabeçalho real.
changelog=(root/'CHANGELOG.md').read_text()
match=re.search(r'^'+re.escape(previous)+r'(?: \([^\n)]*\))?$', changelog, re.M)
assert match is not None, 'Cabeçalho da versão anterior ausente'
previous_header=match.group(0)
assert run([('CHANGELOG.md',previous_header,previous_header+'\n\n- '+first)])!=0
assert run([('RELEASE_NOTES.md',header,previous)])!=0
assert run([('RELEASE_NOTES.md','- '+second,'- Texto removido do release.')])!=0
print('Release news gate: 9 scenarios passed')
