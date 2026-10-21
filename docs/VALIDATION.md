# Validação 0.1.0-alpha.68 (20/10/2026)

- Logs do workflow 48 analisados: na alpha.67 passaram `:core:test`, `:app:testDebugUnitTest`, `:app:assembleDebug`, compilacao e empacotamento dos testes Android. Falha unica bloqueadora: `:app:lintDebug`, `PermissionImpliesUnsupportedChromeOsHardware` por `CAMERA` sem `uses-feature required=false`.
- Correcao limitada ao AndroidManifest: `android.hardware.camera` e `android.hardware.camera.autofocus` explicitamente opcionais, sem suprimir Lint; preservadas permissao CAMERA e funcionalidade de scan QR.
- Adicionados `tools/test_camera_feature_optional.py`, tres cenarios negativos e verificacao estrutural em `tools/validate_source.py`; integrado ao workflow CI.
- Room v11, backup JSON v9, regra de 100%, corrente, historico e monitoramento remoto permanecem inalterados.
- Validacao local: 20 scripts `tools/test_*.py` aprovados, incluindo o contrato opcional da camera (3 cenarios negativos), migracao SQLite 10→11, gate de Novidades (9 cenarios) e contratos de QR/LAN.
- `python3 tools/validate_source.py` e `python3 tools/check_release_news.py`: aprovados; Gradle Wrapper SHA-256 e CRC validos.
- `bash tools/run_external_labs_policy_test.sh`: 16 invariantes Kotlin/JVM aprovados.
- `bash tools/run_math_audit.sh`: 15 conjuntos de verificacoes Kotlin/JVM dos calculos aprovados.
- `tools/test_labs_full_charge.py` atualizado para verificar a novidade introduzida na alpha.67 via CHANGELOG, em vez de exigir a versao 67 na tela de Novidades do build 68.
- Tentativa local `./gradlew :core:test :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug :app:assembleDebug --offline --no-daemon`: bloqueada ANTES do build por `UnknownHostException: services.gradle.org` ao obter Gradle 8.11.1.
- A compilacao da alpha.68, o Lint Android e testes de instalacao/camera precisam ser reconfirmados no GitHub Actions/dispositivo. Nenhuma alegacao de compilacao local bem sucedida.

# Validação 0.1.0-alpha.67 (20/10/2026)

- Adicionada opção de finalização em 100% aos formulários Solar e Elétrico, inclusive testes manuais e iniciados por rede local (ativada por padrão para novos testes).
- Um percentual novo de 100% do Android encerra o teste depois de persistir a leitura; status FULL distingue essa causa de prazo e finalização manual, com notificação onde autorizada. Teste de descarga não sofre alteração.
- Room v11: migração explícita 10→11 adiciona stopAtFull com valor 0 para testes legados, sem alterar duração, corrente, temperatura ou sessões.
- Backup JSON v9 persiste a opção e aceita versões antigas (1..8) sem habilitar retroativamente a regra.
- Testes específicos: `core/.../LabFullChargeTest.kt`, `tools/test_labs_full_charge.py` e `RoomMigrationTest.kt` (incluindo migração desde v10).
- 19 scripts `tools/test_*.py` aprovados localmente; `tools/validate_source.py` e `tools/check_release_news.py` aprovados; `bash tools/run_math_audit.sh` (15 conjuntos), `bash tools/run_external_labs_policy_test.sh` (16 invariantes) e execução isolada de 7 casos Kotlin/JVM da nova regra aprovados.
- O app não desliga fisicamente o carregador; conclusão com a tela apagada depende de serviço foreground ativo e das restrições do Android.
- Build Android/Gradle e instrumentação em aparelho real: pendentes até validação no GitHub Actions.

# Validação 0.1.0-alpha.66 (20/10/2026)

- Workflow 46 (GitHub Actions): :core:test, :app:testDebugUnitTest e :app:assembleDebug **concluíram** na alpha.65; falha em :app:compileDebugAndroidTestKotlin. Não afirmar que o workflow global foi aprovado.
- Erros do Kotlin em `RoomMigrationTest.kt:167`: `No value passed for parameter 'targetMs'` e `No value passed for parameter 'deadlineAt'`, na criação de `LabTest` do teste `carregamentoEletricoPersisteTemperaturasEMedia`.
- Correção: fixture do teste instrumentado inclui `targetMs=60_000L` e `deadlineAt=61_000L`, coerentes com início em 1_000 ms e encerramento em 61_000 ms.
- `tools/test_electric_lab_contract.py` agora verifica os dois argumentos na fixture específica. Nenhuma alteração de lógica de medição, UI, Room v10, backup JSON v8 ou integração remota.
- Validações locais desta entrega: 18 scripts `tools/test_*.py`, `tools/validate_source.py`, `tools/check_release_news.py`, teste do gate de Novidades (9 cenários), 16 testes SQLite, 16 invariantes Kotlin/JVM de integração AIDL e 15 conjuntos de auditoria matemática Kotlin/JVM: **aprovados**.
- `./gradlew :app:assembleDebugAndroidTest --no-daemon --stacktrace` tentado neste ambiente, mas falhou **antes do build** com `UnknownHostException` em `services.gradle.org`, ao tentar obter Gradle 8.11.1.
- Compilação do teste instrumentado foi **corrigida no código, não confirmada por um novo workflow**. Os testes instrumentados ainda não foram executados num dispositivo/emulador.

# Validação 0.1.0-alpha.65 (20/10/2026)

- Implementado o teste de carregamento elétrico no Labs, respeitando leituras reais da bateria. O início exige conexão informada pelo Android e `CHARGING`/`FULL`, além do serviço de monitoramento ativo.
- Média térmica é aritmética das temperaturas válidas, não uma estimativa temporal. Leitura inicial e observações futuras são somadas; a final só é incluída se ainda não tiver sido contabilizada. Leituras sem timestamp novo não são recontadas. Ausência de amostras fica indisponível.
- Room v10: migração explícita 9→10 com soma, contagem e tipo de fonte detectada no Labs, com zeros legados significando média desconhecida. Migrações Android instrumentadas preparadas, execução em Android pendente.
- Backup JSON v8 grava os novos campos; leitura de v1–v7 permanece suportada sem inventar média histórica.
- Os 18 scripts `tools/test_*.py` passaram; `tools/validate_source.py` e `tools/check_release_news.py` também passaram. O teste do SQL de migração executou as três instruções reais `ALTER TABLE`, preservou dois registros legados e comprovou escrita do novo tipo ELECTRIC.
- Análise sintática via Kotlin PSI: 75 arquivos `.kt` com zero erros. Compilação/execução isolada da lógica `LabTemperature` em `kotlinc` 1.9, com média e nulos, aprovada.
- `RoomMigrationTest` inclui teste de migração 9→10 e persistência de testes elétricos em banco Room em memória. Ambos foram escritos, mas ainda não executados no Android.
- Tentativa real de `./gradlew :core:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --no-daemon` falhou antes da compilação por `UnknownHostException: services.gradle.org` ao obter Gradle 8.11.1.
- Compilação Android Gradle, KSP/Room, testes instrumentados, funcionamento da tela e uso no aparelho físico permanecem pendentes até GitHub Actions/Android.

# Validação 0.1.0-alpha.64 (20/10/2026)

- Workflow Pulso-Android-Pulso-Bateria-44 (20/10/2026): falhou antes da compilação Android no `tools/test_release_news_gate.py` por data fixa da alpha.62 (esperava 19/10; CHANGELOG registrava 20/10).
- Corrigido teste negativo para localizar o cabeçalho real da release anterior; verificação mantém reprovação quando novidade atual é reutilizada no histórico.
- Corrigida expectativa antiga de `sem INTERNET` do teste AIDL: AIDL preserva suas restrições, LAN opcional usa INTERNET.
- Protocolo remoto reforçado com IPv4 privado, servidor ligado somente ao IP Wi-Fi escolhido, prazo de conexão/leitura e dados recentes.
- Correção de possível falha de compilação: variável local `request` no envio solar escondia a função `request(endpoint, payload)`.
- `python tools/validate_source.py` e `python tools/check_release_news.py`: aprovados; os 9 cenários de bloqueio de Novidades passaram.
- Todos os 17 scripts locais `tools/test_*.py`: aprovados, incluindo 16 casos SQLite, 27 contratos AIDL e 15 contratos QR/LAN.
- `bash tools/run_external_labs_policy_test.sh`: 16 invariantes Kotlin/JVM aprovados.
- `bash tools/run_math_audit.sh`: 15 conjuntos de verificações Kotlin/JVM de cálculos aprovados.
- 71 arquivos Kotlin analisados por PSI, sem erros sintáticos; `RemoteLabsManager.kt` compilado isoladamente com Kotlin 1.9 e stubs de Android/JSON/repositório, sem erros. Esses resultados **não** substituem a compilação Android com dependências oficiais.
- Tentativa real de `./gradlew :core:test :app:assembleDebug --no-daemon --stacktrace`: interrompida antes da compilação por `UnknownHostException` em `services.gradle.org`, ao baixar Gradle 8.11.1.
- Build Android, testes instrumentados e verificação com dois aparelhos reais ainda pendentes. Não declarar APK funcional nem beta validada.

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
