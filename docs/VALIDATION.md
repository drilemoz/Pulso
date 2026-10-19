# Validação — Pulso Bateria 0.1.0-alpha.50 / versionCode 49

Comparação solar com avisos de qualidade e três testes Kotlin/JUnit adicionados (execução pelo CI pendente).

Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento.

Room v8, backup v6 e AIDL sem alterações. Executar validações locais e registrar resultados; compilação Android e testes em aparelho não executados neste ambiente.

## Execução local alpha.50

- `tools/validate_source.py` e `tools/test_release_consistency.py`: aprovados.
- Todos os scripts `tools/test_*.py`: aprovados, incluindo 15 testes SQLite e contrato estático da comparação.
- Três testes Kotlin/JUnit adicionados para `LabComparisonQuality`; **não executados** localmente sem dependências JUnit/Gradle.
- Compilação Android/Compose, testes instrumentados e teste físico: **não executados**. Verificar no GitHub Actions antes de instalar.
