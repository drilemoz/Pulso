# Pulso Bateria 0.1.0-alpha.38

- **Correção dos testes do workflow 33:** sincronizada a expectativa de `SessionHistoryTextTest` ao limite visual de até **5 minutos** da alpha.37.
- Casos de regressão agora aceitam 180.001 ms e 300.000 ms de intervalo, e rejeitam 300.001 ms.
- Correção restrita aos testes e metadados. A tela Sessões compacta, relatórios e médias históricas, API Labs multiaplicativos AIDL, Room v8 e backup JSON v6 seguem intactos.
- O log anterior documenta falha em `:core:test` antes de terminar o build. **APK ainda depende da compilação no GitHub Actions.**

APK esperado do GitHub Actions: `Pulso-Bateria-v0.1.0-alpha.38-debug.apk`. ZIP contém código-fonte, não APK.
