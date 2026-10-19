# Validação — Pulso Bateria 0.1.0-alpha.40 / versionCode 40

## Escopo

- Ajuste do cartão de recarga na tela inicial.
- Indicador térmico visual com faixas relativas ao limite configurado.
- Sem alterações em Room v8, backup JSON v6, API AIDL v1 ou lógica dos Labs.

## Verificações locais

- Scripts estáticos de navegação, armazenamento e home executados com sucesso.
- Análise sintática do `PulsoScreen.kt` e do utilitário `ThermalBands.kt` sem erros detectados nos contratos locais.
- Testes manuais de regra térmica adicionados em `ThermalBandsTest.kt` para a suíte JVM do núcleo.

## Pendências

Executar workflow da alpha.39 no GitHub Actions; validar compilação Compose, APK, comportamento visual no Android e integração contínua da suíte JVM completa.

Workflow 35: erro de compilação em PulsoScreen.kt linhas 429, 498–499 corrigido. Compilação Android da alpha.40 pendente.
