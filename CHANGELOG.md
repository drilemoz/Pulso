## 0.1.0-alpha.72 (20/10/2026)

- Solar, Elétrico e Consumo começam sem selecionar duração; encerramento e duração efetiva continuam registrados no relatório.
- A estimativa de término aos 100% ganha cartão grande no Labs local e destaque junto à bateria no painel remoto.
- Sem progresso suficiente, a tela informa que aguarda medições; o Consumo mantém encerramento manual sem previsão inventada.
- O formulário remoto deixa de pedir tempo e envia testes solares/elétricos sem prazo, preservando a finalização opcional aos 100%.
- Testes com prazo de versões anteriores mantêm suas datas originais no histórico, sem alterar Room v11 ou backup JSON v9.

## 0.1.0-alpha.71 (20/10/2026)

- Monitoramento remoto abre sem pop-up obrigatória: ações Ser monitorado e Monitorar outro celular ficam visíveis no topo do Labs.
- Aparelhos recentes permite reconectar sem novo QR enquanto a autorização do monitorado continuar válida; credenciais locais são protegidas pelo AndroidKeyStore.
- Carga automática inicia com um toque, sem prazo nem seleção de Solar/Elétrico, preservando a origem informada pelo Android sem atribuir a fonte incorretamente.
- Carga automática finaliza ao atingir 100% ou após três minutos consecutivos de desconexão confirmada, mantendo relatório e lacunas observacionais.
- Painel remoto destaca percentual, ganho na bateria e previsão, recolhendo temperaturas e outros detalhes técnicos sem ocultar os horários do teste.

## 0.1.0-alpha.70 (20/10/2026)

- Labs mostra horário de início, término programado, duração transcorrida e tempo restante nos testes Solar, Elétrico e Consumo.
- O painel remoto recebe os horários reais do aparelho monitorado e diferencia prazo, tempo restante e fim efetivo.
- Testes de carga exibem previsão aproximada de 100% somente com ganho percentual e intervalos de carga suficientes.
- Testes manuais informam ausência de prazo, e relatórios e histórico passam a indicar o término programado.
- Novas verificações protegem horários, leituras antigas, lacunas e a separação entre previsão de carga e prazo do teste.

## 0.1.0-alpha.69 (20/10/2026)

- Monitoramento remoto agora abre escolha entre ser monitorado e monitorar outro aparelho, com troca de função explícita.
- Leitor QR foi integrado à janela vertical do Labs, com permissão de câmera sob demanda e controle da lanterna.
- Controle LAN passa a iniciar testes solares e elétricos, com duração, condições e finalização opcional aos 100%.
- Aparelho monitorado identifica o nome informado pelo controlador, mostra notificação de acesso e permite revogar pela tela ou notificação.
- O novo pareamento usa QR temporário de uso único e sessão separada para reconexão; relatório permanece no aparelho medido.

## 0.1.0-alpha.68 (20/10/2026)

- Manifesto declara a câmera opcional para o leitor de QR Code do Labs, corrigindo o erro Android Lint do workflow 48.
- O aplicativo continua instalável em aparelhos sem câmera, sem impedir o monitoramento de bateria e os testes locais.
- Uma regressão automática verifica que as permissões de câmera e o hardware opcional permaneçam declarados corretamente.
- Compilação Debug e testes unitários da alpha.67 já estavam aprovados no workflow 48; esta release corrige a etapa de Lint.

## 0.1.0-alpha.67 (20/10/2026)

- Labs Solar e Carregamento Elétrico recebem Finalizar ao atingir 100%, ligado por padrão e disponível mesmo na duração manual.
- Uma nova leitura válida de 100% encerra automaticamente o teste, preserva os valores observados e emite a notificação de conclusão.
- O histórico e o relatório distinguem término por bateria cheia, por prazo e por ação manual, sem desligar fisicamente o carregador.
- Room v11 e backup JSON v9 preservam a escolha individual do teste, com migração que não ativa a função em registros antigos.

## 0.1.0-alpha.66 (20/10/2026)

- Corrigida a criação do teste Android de Carregamento Elétrico: duração alvo e prazo final agora são informados ao LabTest.
- Adicionada regressão que bloqueia builds quando faltarem os parâmetros obrigatórios no teste instrumentado.
- Correção baseada no workflow 46: o aplicativo Debug compilou, mas a compilação dos testes Android falhou; medições e banco foram preservados.

## 0.1.0-alpha.65 (20/10/2026)

- Labs recebe o Teste de Carregamento Elétrico com duração manual ou programada e verificação da fonte conectada informada pelo Android.
- O carregamento elétrico registra bateria inicial e final, tempo efetivo carregando, corrente líquida média e lacunas observacionais.
- Temperaturas inicial, média aritmética de leituras válidas, final e máxima ficam no relatório e histórico do Labs.
- Room v10 migra a tabela Labs preservando resultados anteriores sem inventar médias térmicas retroativas.
- Backup JSON v8 preserva a média térmica e a origem elétrica, mantendo leitura de backups antigos.

## 0.1.0-alpha.64 (20/10/2026)

- Corrigida colisão entre o comando JSON e o método de envio, que impediria compilar a inicialização solar remota.
- Corrigido o teste de Novidades para reconhecer a versão anterior sem depender de uma data fixa no CHANGELOG.
- O contrato de integração AIDL permanece protegido enquanto a verificação diferencia a comunicação LAN autorizada.
- Pareamento remoto agora aceita somente IPv4 privado e o servidor escuta apenas na interface Wi-Fi selecionada.
- Conexões LAN têm prazo máximo de tentativa e leituras remotas antigas não são apresentadas como atuais.
- A tela remota identifica medições reais, bloqueia comandos quando desconectada e solicita monitoramento no celular A.

## 0.1.0-alpha.63 (20/10/2026)

- Labs ganha Monitoramento remoto com geração de QR Code no celular monitorado e leitura no celular controlador.
- A conexão funciona pela mesma rede Wi‑Fi local, sem internet, exibindo bateria, corrente, temperatura e tempo do teste remoto.
- O controlador pode iniciar e finalizar remotamente o teste solar com configuração rápida de céu, painel e duração.
- A interface do Monitoramento remoto adota o layout em dois cartões Celular A/Celular B e o painel compacto de acompanhamento pedido para a beta.
- A release adiciona permissões e dependências locais para QR Code, câmera e comunicação LAN do recurso remoto.

## 0.1.0-alpha.62 (20/10/2026)

- Home e Sessões agora calculam as médias históricas usando somente sessões finalizadas, sem misturar a sessão ativa.
- Transições de carga e descarga não somam o intervalo anterior na duração da nova sessão.
- Agrupamentos de sessões somam apenas minutos observados, mostram lacunas separadas e recusam sequências inconsistentes.
- Contagem de recargas por tipo de fonte acompanha a janela histórica realmente usada na previsão.
- Testes numéricos cobrem somas de duração, variações percentuais, médias ponderadas e trocas de estado.

## 0.1.0-alpha.61 (19/10/2026)

- Corrigido o teste instrumentado de migração 8→9: agora cria corretamente as colunas legadas do Labs.
- Adicionado teste Android para comprovar o pico térmico persistido em sessões de carga e descarga sem amostras brutas.
- Captura PNG e exportação de sessões impedem operações concorrentes e desativam seus botões durante o processamento.
- Relatório CSV agora inclui também datas e horários locais legíveis, além dos timestamps originais.

## 0.1.0-alpha.60 (19/10/2026)

- Temperatura máxima agora é registrada a cada leitura válida nas sessões, mesmo após descartar amostras antigas.
- Exportação CSV identifica se o pico veio do registro contínuo ou somente das amostras históricas retidas.
- Capturas PNG liberam a memória mesmo em caso de erro; exportação e ícone de câmera foram revisados.
- Migração Room 8→9 preserva os dados anteriores e o backup JSON v7 mantém os novos picos sem inventar valores antigos.
- Corrigida a inicialização compartilhada do banco usada pela exportação; pico térmico aparece também no cartão da sessão.

## 0.1.0-alpha.59 (19/10/2026)

- Sessões exibem a temperatura máxima observada nas amostras disponíveis de carga e descarga.
- Um botão de câmera no topo de todas as telas permite salvar uma captura PNG.
- A aba Sessões permite exportar relatórios CSV com temperaturas e dados registrados.
- Room v8, backup JSON v6 e AIDL v1 preservados; máximo limitado às amostras retidas.

## 0.1.0-alpha.58 (19/10/2026)

- A tela Novidades passa a exigir correspondência também com as notas de release da versão atual.
- O build rejeita notas de release desatualizadas ou sem as novidades anunciadas.
- Testes de regressão cobrem a ausência de itens e versões divergentes nas notas de release.
- Room v8, backup JSON v6, AIDL v1 e medições preservados.

## 0.1.0-alpha.57 (19/10/2026)

- Novidades rejeita itens copiados de versões anteriores para evitar anúncios repetidos.
- A validação exige alterações exclusivas do build atual, registradas no CHANGELOG.
- Testes de regressão verificam a rejeição de novidades reaproveitadas de releases antigos.
- Room v8, backup JSON v6, AIDL v1 e cálculos de bateria preservados.

## 0.1.0-alpha.56 (19/10/2026)

- As Novidades agora devem corresponder às alterações registradas no CHANGELOG deste build.
- O build falha se uma novidade estiver ausente do CHANGELOG da versão atual.
- Testes de regressão cobrem divergência entre Novidades e CHANGELOG.
- Preservados Room v8, backup JSON v6, AIDL v1 e cálculos de medição.

## 0.1.0-alpha.55 (19/10/2026)

- Reforçada a verificação obrigatória das Novidades: rejeita itens duplicados, vazios ou malformados.
- Adicionados testes negativos para versão divergente, lista inválida e ausência da entrada de release no CHANGELOG.
- Preservados Room v8, backup JSON v6, AIDL v1 e medições.

## 0.1.0-alpha.54 (19/10/2026)

- Nova tela Novidades: exibe somente as alterações desta versão e abre uma vez por versão instalada, com acesso permanente pelo ícone na barra superior.
- Validação obrigatória no preBuild e CI impede compilação quando a versão das Novidades não corresponde ao Gradle, quando a lista está vazia ou quando falta a entrada no CHANGELOG.
- Room v8, backup JSON v6 e AIDL v1 preservados.

## 0.1.0-alpha.53 (19/10/2026)

- Labs: observação de fluxos passa a respeitar STARTED/STOPPED via collectAsStateWithLifecycle, reduzindo processamento fora da tela.
- Cronômetro visual de teste ativo pausa atualizações em segundo plano e sincroniza ao voltar, sem interferir no registro do teste pelo monitor.
- Room v8, backup JSON v6 e AIDL v1 preservados.

## 0.1.0-alpha.52 (19/10/2026)

- Histórico: agrupamento e títulos de datas passam a considerar explicitamente o locale no cache Compose; preserva sessões, filtros e lacunas observadas.
- Room v8, backup JSON v6 e AIDL v1 inalterados.

## 0.1.0-alpha.51 (19/10/2026)

- Labs: listas de testes concluídos e solares memorizadas por histórico, evitando refiltragem a cada atualização do cronômetro; resultados e persistência intactos.
- Room v8, backup JSON v6 e AIDL v1 inalterados.

## 0.1.0-alpha.50 (19/10/2026)

- Labs: avisos conservadores para comparação A/B com cobertura baixa, duração desigual e medições ausentes. Testes Kotlin e contrato estático no CI. Sem mudança em Room, backup ou cálculo de corrente.

## 0.1.0-alpha.49 (19/10/2026)
- Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento.
- Room v8, backup JSON v6 e API AIDL inalterados.

## 0.1.0-alpha.48 (19/10/2026)
- Barras de duração proporcional na comparação solar, com segmentos carregando, sem carga e sem leitura; nenhum gráfico de corrente sem amostras temporais.
- Room v8, backup JSON v6 e API AIDL inalterados.

## 0.1.0-alpha.47 (19/10/2026)
- Labs: comparação de dois testes solares concluídos com dados observados e aviso de limitações.
- Room v8, backup v6, cálculo e AIDL preservados.

## 0.1.0-alpha.46 (19/10/2026)

- Contexto de evidência das previsões de carga e descarga, sem alterar o cálculo.
- Testes Kotlin da classificação e atualização do versionamento.
- Room v8, backup v6 e AIDL inalterados.

## 0.1.0-alpha.45 (19/10/2026)
- Classifica a origem da corrente como instantânea, média, estimativa ou indisponível na tela de diagnóstico.
- Sinaliza consultas antigas (>60 s), horário inválido e relógio retrocedido, sem alegar conhecer a idade interna do sensor.
- Acrescenta testes JVM para classificação, limites temporais e indisponibilidade; não altera cálculo de corrente, Room v8, backup v6 nem AIDL.

## 0.1.0-alpha.44 (19/10/2026)
- Exibe idade da última consulta ao Android no diagnóstico de leituras brutas, sem confundir consulta recente com atualização do sensor.
- Preserva cálculos, Room v8, backup v6 e API AIDL.

## 0.1.0-alpha.43 (19/10/2026)
- Corrige duplicação histórica das entradas alpha.41/42 no CHANGELOG.
- Adiciona auditoria de versionamento, sequência de changelog e arquivos de release no CI.
- Preserva cálculos, interface, Room v8, backup v6 e API AIDL.

## 0.1.0-alpha.42 (19/10/2026)
- Acrescenta teste de regressão para anotações Compose da Home, evitando repetir os erros do workflow 35.
- Corrige a entrada histórica alpha.40 no CHANGELOG, antes duplicada como alpha.41.
- Preserva Room v8, backup JSON v6, cálculos e contrato AIDL.
- Compilação Android e testes físicos ainda pendentes.

## 0.1.0-alpha.41 (19/10/2026)
- Sincroniza AGENTS.md e acrescenta verificação automática de versão atual e próxima.
- Executa teste de política AIDL/Labs no CI, além dos contratos já verificados.
- Preserva Room v8, backup v6, cálculos e interface da alpha.40.
- Build Android completo e testes físicos ainda pendentes.

## 0.1.0-alpha.40 (19/10/2026)
- Corrige anotações Compose de ThermalUi e SmallInfoChip após falha do workflow 35.
- Mantém indicador térmico e título de recarga da alpha.39.

## 0.1.0-alpha.39 (18/10/2026)

- Corrige o texto longo do cartão da tela inicial, trocando **Média equivalente de 0 a 100%** por **Carga média 0–100%** para evitar quebra visual ruim.
- Adiciona faixas visuais de temperatura na tela inicial com cores e rótulos relativos ao limite configurado pelo usuário.
- Inclui utilitário `ThermalBands` e testes do classificador térmico.
- Atualiza `versionName`, `versionCode` e documentação para alpha.39.

## 0.1.0-alpha.38 (19/10/2026)

- Corrige o workflow 33 ajustando o teste de agrupamento ao limite atual de 5 minutos.
- Mantém a regra de produção, a interface compacta de Sessões, a API multiaplicativos e os demais recursos intactos.
- Atualiza `versionName`, `versionCode`, workflow e documentação para alpha.38.
