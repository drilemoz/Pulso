#!/usr/bin/env python3
"""Fail build if release notes are missing or belong to a different build."""
from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
gradle = (root/'app/build.gradle.kts').read_text()
news = (root/'app/src/main/java/br/com/pulsobateria/ui/ReleaseNews.kt').read_text()
changelog = (root/'CHANGELOG.md').read_text()
name = re.search(r'versionName\s*=\s*"([^"]+)"',gradle)
code = re.search(r'versionCode\s*=\s*(\d+)',gradle)
news_name = re.search(r'const val VERSION_NAME\s*=\s*"([^"]+)"',news)
news_code = re.search(r'const val VERSION_CODE\s*=\s*(\d+)',news)
assert all((name,code,news_name,news_code)), 'Version fields missing'
assert (name.group(1),code.group(1)) == (news_name.group(1),news_code.group(1)), 'Novidades desatualizadas: atualize VERSION_NAME e VERSION_CODE'
items = re.search(r'val ITEMS\s*=\s*listOf\((.*?)\n \)',news,re.S)
assert items, 'Lista de Novidades ausente'
entries = re.findall(r'^\s*"([^"\n]*)"\s*,?\s*$', items.group(1), re.M)
assert entries and all(len(e.strip()) >= 12 for e in entries), 'Novidades vazias ou curtas'
assert len(entries) == len(set(e.casefold().strip() for e in entries)), 'Novidades duplicadas'
assert len(entries) == len(re.findall(r'^\s*"', items.group(1), re.M)), 'Item de Novidades malformado' 
headings = re.findall(r'^## (\S+)',changelog,re.M)
assert headings and headings[0] == name.group(1), 'CHANGELOG deve começar pela versão do build'
assert len(headings)==len(set(headings)), 'Versões duplicadas no CHANGELOG'
print('Release News OK:',name.group(1), 'code',code.group(1))
