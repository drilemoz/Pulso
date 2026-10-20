# Auditoria pré-beta 0.1.0-alpha.63 (20/10/2026)

- Implementado o Labs → Monitoramento remoto com dois papéis locais: Celular A (gera QR Code) e Celular B (escaneia QR Code).
- A conexão entre os aparelhos foi estruturada para LAN/Wi‑Fi local, sem internet, com pareamento temporário contendo IP, porta e token em QR Code.
- O painel remoto mostra bateria, corrente líquida, temperatura, tempo de teste e resumo de períodos carregando / sem carregamento / sem leitura.
- O controlador remoto pode iniciar e finalizar teste solar com configuração rápida de céu, painel e duração. O início remoto tenta subir o monitoramento do aparelho remoto, mas depende das permissões e restrições do Android no aparelho monitorado.
- `python tools/check_release_news.py`: aprovado.
- `python tools/validate_source.py`: aprovado após sincronizar versões, workflow, permissões e documentação da alpha.63.
- Tentativa real de `./gradlew :app:assembleDebug --no-daemon`: falhou antes da compilação por `UnknownHostException` ao acessar `services.gradle.org` para baixar a distribuição do Gradle Wrapper.
- Build Android, lint, testes Kotlin/JUnit oficiais do Gradle, testes instrumentados de câmera/QR/rede local e validação física entre dois aparelhos: pendentes neste ambiente.
- O recurso remoto usa `INTERNET` e `CAMERA`, portanto esta alpha deixa de ser estritamente “sem INTERNET”; a comunicação prevista é apenas local na mesma rede Wi‑Fi.

# Validação 0.1.0-alpha.61 (19/10/2026)

- Revisão da alpha.60 e correção da fixture Android de Room v8: o nome de coluna gerado continha `$`, impedindo o teste instrumentado de representar o banco legado real.
- Teste instrumentado adicional de pico térmico para carga e descarga, inclusive quando o ponto bruto não é salvo. Teste escrito; execução em dispositivo/emulador ainda pendente.
- Guardas de concorrência nos botões de captura PNG e exportação CSV; relatório inclui data/hora local sem abandonar epoch ms.
- Room v9, backup JSON v7, AIDL v1 e cálculos preservados.
- `python tools/validate_source.py`, `python tools/check_release_news.py`, `python tools/test_release_consistency.py`: aprovados.
- 15 scripts `tools/test_*.py`: aprovados; o gate de Novidades passou em 9 cenários e os 16 testes SQLite locais passaram (avisos ResourceWarning antigos de conexões não fechadas não alteraram o resultado).
- `bash tools/run_external_labs_policy_test.sh`: 16 invariantes Kotlin/JVM aprovados.
- Kotlin PSI: 46 arquivos `.kt` de produção e teste analisados sem erro de sintaxe; isto não equivale a compilar o Android.
- Tentativa real de `./gradlew :core:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --offline --no-daemon`: não iniciou build devido a falha ao obter distribuição do Gradle Wrapper na rede.
- Build Android, instrumentação Room em dispositivo/emulador, backup e comportamento do PNG/CSV em aparelho: pendentes.

# Validação 0.1.0-alpha.60 (19/10/2026)

- Revisão da alpha.59: o pico térmico calculado a partir de amostras retidas não garantia detectar o maior valor real já recebido pelo app. Agora o resumo de cada sessão mantém a máxima de cada leitura válida, dentro de -40..100 °C, mesmo após poda das amostras (não recupera intervalos sem coleta).
- Room 8→9 adiciona `sessions.maxTemperatureC` anulável, sem preencher dados antigos artificialmente. Novos backups JSON v7 preservam esse pico; leitor aceita também formatos 1..6.
- Exportação CSV separa picos do monitoramento contínuo após alpha.60 e picos apenas das amostras retidas em sessões legadas. Utiliza agregação SQL por sessão, sem carregar todas as amostras na RAM.
- Sessões com pico agregado mostram a máxima já no cartão recolhido; detalhes mantêm indicação de histórico parcial e lacunas.
- CSV protege campos textuais iniciados por caracteres de fórmula para reduzir risco ao abrir relatórios em planilhas.
- Corrigida falha de compilação preexistente na alpha.59: `PulsoViewModel` acessava `app.database`, mas o banco era variável local do `PulsoApp`. Agora é propriedade `lateinit` inicializada em `onCreate`.
- Captura de tela desenha a View na thread principal e grava/comprime o PNG fora dela, com liberação do Bitmap mesmo quando a gravação falha. Ícone local em vez de `material-icons-extended` não declarado; botão CSV sem ícone externo.
- `python tools/validate_source.py`: aprovado (versão, Wrapper SHA-256/CRC, estrutura, 7 XML).
- `python tools/check_release_news.py`: aprovado; `python tools/test_release_news_gate.py`: 9 cenários aprovados (8 negativos e 1 positivo).
- 15 scripts `tools/test_*.py`: aprovados, incluindo 16 testes SQLite de migrações/retenção, nova regressão de `app.database` e contratos de CSV/captura/temperatura. Os testes SQLite registram ResourceWarning de conexões legadas que não afetam os resultados.
- `bash tools/run_external_labs_policy_test.sh`: 16 invariantes Kotlin/JVM aprovados.
- Compilação com Kotlin 1.9 do arquivo `SessionThermalPeak.kt` + 6 verificações de execução isolada: aprovada. Os testes JUnit novos estão escritos, porém não foram executados com Gradle.
- Tentativa de `./gradlew :core:test :app:assembleDebug --offline --no-daemon`: **não iniciou o build**, pois o Gradle Wrapper tentou acessar `services.gradle.org` e encontrou `UnknownHostException`. Portanto **não há APK validado**.
- Android Gradle/KSP, teste de migração Room instrumentado, teste do backup 6→7, instalação e testes físicos: **pendentes**.

# Validação 0.1.0-alpha.59 (19/10/2026)

Verificações estáticas e gate de Novidades executados nesta entrega; compilação Gradle e teste Android em aparelho pendentes. A máxima histórica é limitada às amostras retidas, não representa pico garantido.

# Validação 0.1.0-alpha.58 (19/10/2026)

- `python tools/check_release_news.py`: aprovado; sincronização Gradle, Novidades, CHANGELOG e RELEASE_NOTES.
- `python tools/test_release_news_gate.py`: 9 cenários aprovados, incluindo versões divergentes e notas de release incompletas.
- `python tools/validate_source.py`: aprovado; 14 fontes obrigatórias, 7 XML, versões, permissões e SHA-256/CRC do Gradle Wrapper.
- `python tools/test_release_consistency.py`: aprovado; 21 entradas de release únicas e ordenadas.
- Todos os 14 scripts `tools/test_*.py`: aprovados na execução local (inclui SQLite e contratos estáticos).
- Gradle Android, testes Kotlin/JUnit e instrumentados, APK e testes físicos: **não executados** neste ambiente.
- Room v8, backup JSON v6, AIDL v1 e fórmulas de medição preservados.
