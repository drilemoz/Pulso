# Regras do projeto Pulso Bateria

- Android Kotlin/Compose, offline, sem anúncios/rastreamento; INTERNET exclusivamente para LAN local opcional em Labs. Não inventar medições ou capacidades do aparelho.
- Antes de editar, ler README, docs/MEASUREMENTS.md e docs/VERSIONING.md.
- Versão atual 0.1.0-alpha.70 / versionCode 70. Próxima 0.1.0-alpha.71 / 71. Incrementar sequencialmente cada entrega e sincronizar todos os documentos, Gradle, workflow e nome do ZIP.
- Preservar applicationId. Keystore privada e Secrets nunca entram no ZIP de código ou repositório. Para Debug atualizável, requer Secrets estáveis; sem Secrets a chave temporária deve receber aviso.
- Manter cálculos no módulo core, testáveis sem Android; validar nulos, sentinelas, sinal, relógio e lacunas.
- Não iniciar foreground service fora de ação permitida; manter tipo specialUse e sua justificativa, sem simular dataSync/health. Não adicionar wake locks, permissões privilegiadas ou mecanismos de evasão.
- Histórico só contém períodos observados. Não preencher lacunas. Migrações Room explícitas; manter schemas versionados.
- Rodar `./gradlew :core:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` quando possível. Distinguir sucesso real de pendência no relatório.
- Entregar ZIP com os arquivos na raiz, incluindo .github, Wrapper JAR e scripts. Excluir caches/build/local.properties/keystores. Verificar CRC do ZIP e SHA-256 do Wrapper.
- Qualquer revisão futura deve atualizar docs/VALIDATION.md com o que foi efetivamente executado e limitações restantes.

- Na alpha.21, páginas internas compactam explicações, mas avisos de dados insuficientes/parciais continuam visíveis. Não modificar cálculos/Room/backup sem necessidade.

- Alpha.22: notificação compacta/expandida sem duplicar corrente, média zero ambígua oculta em carga/descarga, estado FULL dependente do Android e aviso térmico passivo. Testar 100% e fontes alternativas.

- Alpha.23: Labs com Room v6, backup JSON v4, prazos e lacunas observacionais. Finalização automática pode atrasar sob restrições do Android; testar em aparelho real e não alegar precisão de timer/wake lock inexistente.

- Alpha.24: Labs apresenta menu e Teste de Consumo. Room v7 (6→7) com legado solar preservado, backup v5. Testes de consumo com fonte externa detectada são interferidos; não inventar amostras durante lacunas.

- Alpha.25: Configurações usam menu por categoria e controles compactos, com temas claro/AMOLED harmonizados. Preservar todas as ações (incluindo permissão de alertas, backup e diagnóstico) e manter avisos importantes visíveis. Sem migração Room/backup.

- Alpha.26: Labs permite excluir apenas teste concluído por ID mediante confirmação; cartão mostra Início/Fim juntos e aviso quando não houve intervalo observado. Room v7 / backup v5 inalterados.

- Alpha.27: Labs Room v8 com migração 7→8 e backup JSON v6; temperatura da bateria vem apenas de leituras reais (initial/final/max opcionais). Campos de contexto são declarados manualmente. Ação copiar é textual, excluir mantém confirmação. Não preencher valores legados.

- Alpha.28: Home apresenta hora estimada de descarga e média com 100% calculadas no core usando apenas histórico válido, mantendo previsão de carga anterior. Room v8 / backup v6 sem alteração. Próxima alpha.30 / 30.

- Alpha.29: Home inclui previsão histórica de recarga independente da descarga; apenas CHARGING, com preferências por fonte detectada e faixa 80..100 observada. Sem banco novo.

- Alpha.30: Home redesenhada no estilo compacto, com resumo superior mais enxuto, medidor reduzido e cartão de previsão promovido para a primeira dobra. Mudança visual somente; Room v8, backup v6 e cálculos do core intactos. Próxima alpha.31 / 31.

- Alpha.31: três cartões inferiores da Home compactados visualmente; preservar filtro 5/20/60, detalhes independentes de corrente, acessibilidade do gráfico e acesso à análise completa. Lógica, Room v8 e backup v6 inalterados. Próxima alpha.32 / 32.

- Alpha.32: agrupar sessões somente na UI, com origem preservada, toggle opt-out, lacunas não somadas e badge ATUAL apenas em leitura recente. Core provê particionamento testável. Não mesclar linhas reais do Room. Próxima alpha.33 / 33.

- Alpha.33: remover apenas a aba Sessões da navegação; histórico consultável a partir da Home, sem apagar registros. Labs com condições adicionais recolhíveis, CTA persistente e entradas numéricas validadas. Room v8 e backup v6 intactos. Próxima alpha.34 / 34.

- Alpha.34: a navegação usa Início/Sessões/Labs/Config.; **Uso foi removida da barra**, não os dados. O histórico ocupa a aba 1 e permanece acessível pela Home. Labs conserva melhorias da alpha.33. Room v8 e backup v6 inalterados. Próxima alpha.35 / 35.

- Alpha.35: AIDL externo apenas para um cliente instalado explicitamente pareado, com UID do Binder coletado antes da troca de dispatcher e certificado SHA-256 conferido a cada chamada. Não ativar FGS remoto, não incluir API HTTP, não incluir autorizados no backup, proteger testes manuais e preservar Room v8/JSON v6. Contrato v1 documentado em docs/INTEGRACAO_VIGIA_IA.md. Próxima alpha.36 / 36.

- Alpha.36: API multiaplicativos mantém AIDL v1, DataStore migra cliente legado alpha.35, por-app signers/IDs, limite 20, recusa UID compartilhado e revogação individual/geral. Não alterar Room/backup/cálculo. Próxima alpha.37 / 37.

- Alpha.37: UI Sessões apresenta cinco filtros/ações em uma linha, “Unir ≤5 min” ao lado da ajuda, histórico equivalente completo de carga e descarga só de sessões finalizadas elegíveis. Apenas `SessionHistoryText` altera limite visual para 300_000 ms; regras do monitor e Room permanecem intactas. Próxima alpha.38 / 38.

- Alpha.38: corrigido teste legado de SessionHistoryText sobre intervalo visual de cinco minutos (CI workflow 33 falhou em :core:test). Mantidos 300_000 ms inclusivos em produção; teste de fronteira 300_001 ms. Sem mudanças de banco, cálculos, tela ou AIDL. Próxima alpha.39 / 39.

- Alpha.42: teste de regressão Compose da Home e correção histórica do CHANGELOG. Room v8 e backup v6 preservados. Próxima alpha.43 / 43.
- Alpha.41: reforço de validação CI, verificação de versão do AGENTS e documentação sincronizada. Room v8, backup v6, cálculos e AIDL intactos. Próxima alpha.43 / 43.

- Alpha.45: classificação diagnóstica da fonte e aviso de consulta antiga (>60 s). Não interpretar consulta recente como leitura nova do sensor. Próxima alpha.47 / 47.

- Alpha.46: classificação de evidência das previsões apenas na UI; não alterar taxas/ETA ou persistência. Próxima alpha.47 / 47.

- Alpha.47: comparação A/B de testes solares concluídos apenas na UI; sem interpolação de lacunas, Room/backup preservados. Próxima alpha.48 / 48.

- Alpha.48: Barras de duração proporcional na comparação solar, com segmentos carregando, sem carga e sem leitura; nenhum gráfico de corrente sem amostras temporais. Próxima alpha.49 / 49.

- Alpha.49: Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento. Próxima alpha.50 / 50.

- Alpha.50: Labs sinaliza limitações da comparação solar com critérios conservadores (cobertura <80%, dados ausentes, duração >25% diferente); não inventar qualidade percentual nem modificar Room/backup. Próxima alpha.51 / 51.

- Alpha.51: Labs: listas de testes concluídos e solares memorizadas por histórico, evitando refiltragem a cada atualização do cronômetro; resultados e persistência intactos. Próxima alpha.52 / 52.

- Alpha.52: Histórico: agrupamento e títulos de datas passam a considerar explicitamente o locale no cache Compose; preserva sessões, filtros e lacunas observadas. Próxima alpha.53 / 53.

- Alpha.53: Labs coleta fluxos somente em STARTED; timer visual não executa em segundo plano. A coleta persistente do monitor não depende da tela. Próxima alpha.54 / 54.

- Alpha.54: Novidades por build em ReleaseNews.kt; atualizar VERSION_NAME, VERSION_CODE e ITEMS a cada versão, além de CHANGELOG. tools/check_release_news.py bloqueia preBuild e CI se houver divergência.

- Alpha.55: verificação de Novidades reforçada e testes negativos obrigatórios; atualizar conteúdo por build. Próxima alpha.57 / 56.

- Alpha.56: gate de Novidades exige que cada item corresponda a um bullet no CHANGELOG da versão atual, impedindo notas sem correspondência; atualizar teste a cada release. Próxima alpha.57 / 57.

Alpha.57: Novidades exclusivas do build, sem reutilização literal de itens antigos do CHANGELOG; teste de regressão do bloqueio. Próxima 0.1.0-alpha.58 / 58.

- Alpha.58: gate também valida RELEASE_NOTES e seus itens exclusivos; próxima alpha.59 / 59 somente após avaliação beta.

- Alpha.59: máximo térmico das amostras persistidas por sessão; botão de captura PNG em todas as telas; CSV de relatórios via SAF, sem alterar Room/backup. Próxima alpha.60 / 60.

- Alpha.60: Room v9 adiciona máximo térmico persistente por sessão, calculado a cada leitura válida; legados ficam NULL até leituras pós-migração. Backup JSON v7 conserva máximo e aceita arquivos 1..6. A captura usa ícone local e IO fora da UI; CSV revela origem parcial. Gate de Novidades obrigatório. Próxima alpha.61 / 61.

- Alpha.61: testes instrumentados do Room corrigidos (nomes de coluna na preparação da v8), cobertura do pico em carga/descarga, captura e exportação sem concorrência e CSV com datas locais. Room v9/JSON v7 sem alterações; próxima alpha.63 / 62.

- Alpha.62: Home/Sessões usam apenas sessões finalizadas para médias históricas; transição de estado não incorpora tempo do modo anterior; somas de agrupamentos observacionais validadas no core e teste; fonte de recarga apresenta contagem da janela usada. Não alterar Room v9, backup JSON v7 e AIDL v1. Próxima alpha.64 / 64.

- Alpha.65: teste elétrico do Labs com média aritmética apenas das temperaturas observadas, Room v10 (9→10) e backup JSON v8. Entrada exige carregador identificado pelo Android; categoria USB não comprova energia da tomada. Novidades sincronizadas com o build.

- Alpha.66: workflow 46 identificou erro de compilação do teste instrumentado RoomMigrationTest (LabTest sem targetMs/deadlineAt). Fixture corrigida e contrato estático exigido. Produção, Room v10, backup JSON v8, AIDL e cálculos preservados. Próxima alpha.67 / 67.

- Alpha.67: Solar e Elétrico finalizam com nova leitura 100% se opção ativa, por padrão em novos testes. Status FULL diferencia do prazo. Room v11 e backup JSON v9 incluem stopAtFull; legado migra false. Teste de consumo não usa regra; não interromper energia física. Próxima alpha.68 / 68.

- Alpha.68: corrigido Android Lint do leitor QR com hardware de camera/autofoco opcionais no Manifest; teste estatico e gate de versao garantem essa declaracao. Mantem Room v11/backup v9, medicao e regra de 100%. Proxima alpha.69 / 69.

- Alpha.69: remoto LAN v2 tem QR de uso único, janela vertical de scanner, papéis explícitos, suporte solar/elétrico, revogação local/notificação. Não persistir credenciais no backup, não criar serviço secundário, não inferir dados em lacunas. Próxima alpha.70 / 70.

- Alpha.70: Labs mostra início civil, prazo programado, decorrido, restante e projeção aproximada para 100% só com evidência de carga. Remoto transmite horários civis do monitorado; histórico mantém prazo original. Room v11, backup JSON v9, persistência e cálculos anteriores mantidos. Próxima alpha.71 / 71.
