#!/usr/bin/env python3
"""Regression tests for the release-news build gate."""
import tempfile, pathlib, shutil, runpy, contextlib, io
root = pathlib.Path(__file__).resolve().parents[1]
paths = ['app/build.gradle.kts','app/src/main/java/br/com/pulsobateria/ui/ReleaseNews.kt','CHANGELOG.md','tools/check_release_news.py']
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
assert run([(news,'const val VERSION_CODE = 55','const val VERSION_CODE = 54')])!=0, 'Old news version must fail'
assert run([(news,'"Validação reforçada das Novidades: verifica duplicações e textos vazios.",','"",')])!=0, 'Empty news item must fail'
assert run([(news,'"Teste de regressão garante que versões e notas incorretas interrompam o build.",','"Validação reforçada das Novidades: verifica duplicações e textos vazios.",')])!=0, 'Duplicate item must fail'
assert run([('CHANGELOG.md','## 0.1.0-alpha.55','## 0.1.0-alpha.54')])!=0, 'Wrong changelog heading must fail'
print('Release news gate: 5 scenarios passed')
