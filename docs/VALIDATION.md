# Validação — Pulso Bateria 0.1.0-alpha.45 / versionCode 45

## Alterações
Classificação de origem e atualidade da consulta no diagnóstico, com lógica pura no core e testes de fronteira. Room v8, backup v6 e AIDL preservados.

## Verificações locais
Executados: validate_source.py, test_release_consistency.py, todos os tools/test_*.py (incluindo 15 testes SQLite), e verificação CRC do ZIP com 143 arquivos. Os testes Kotlin/JVM novos foram escritos, mas não executados localmente por falta de toolchain/Gradle.

## Pendências
- Build Android completo, lint, testes instrumentados e instalação física ainda não comprovados.
- Geração e versionamento dos schemas Room oficiais pelo KSP ainda precisam de conferência.
