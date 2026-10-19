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
assert run([(news,'const val VERSION_CODE = 57','const val VERSION_CODE = 55')])!=0, 'Old news version must fail'
assert run([(news,'"Novidades rejeita itens copiados de versões anteriores para evitar anúncios repetidos.",','"",')])!=0, 'Empty news item must fail'
assert run([(news,'"A validação exige alterações exclusivas do build atual, registradas no CHANGELOG.",','"Novidades rejeita itens copiados de versões anteriores para evitar anúncios repetidos.",')])!=0, 'Duplicate item must fail'
assert run([('CHANGELOG.md','## 0.1.0-alpha.57','## 0.1.0-alpha.55')])!=0, 'Wrong changelog heading must fail'
assert run([(news,'Novidades rejeita itens copiados de versões anteriores para evitar anúncios repetidos.','Texto diferente do CHANGELOG nesta versão, portanto deve falhar.')])!=0, 'News missing from changelog must fail'
assert run([('CHANGELOG.md','## 0.1.0-alpha.56 (19/10/2026)', '## 0.1.0-alpha.56 (19/10/2026)\n\n- '+"Novidades rejeita itens copiados de versões anteriores para evitar anúncios repetidos." )])!=0, 'Reused item from older release must fail'
print('Release news gate: 7 scenarios passed')
