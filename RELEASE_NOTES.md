## 0.1.0-alpha.73

- O painel remoto apresenta cartões de corrente e temperatura com a mesma altura e unidades na mesma linha.
- O ganho de bateria no controle remoto passa a ser exibido em %, com indicação de Carga automática quando aplicável, sem usar p.p. na interface.
- Uma nova notificação no celular controlador avisa quando a carga monitorada chega a 100%, perde a fonte ou termina, sem repetir alertas por atualização.
- A notificação de conclusão remota solicita permissão no controlador após o pareamento e abre o Labs; o relatório continua salvo no aparelho monitorado.
- A auditoria confirma que uma nova leitura real de 100% é persistida antes de encerrar o teste solar, elétrico ou automático; sem alterações no Room ou backup.

## 0.1.0-alpha.72

- Solar, Elétrico e Consumo começam sem selecionar duração; encerramento e duração efetiva continuam registrados no relatório.
- A estimativa de término aos 100% ganha cartão grande no Labs local e destaque junto à bateria no painel remoto.
- Sem progresso suficiente, a tela informa que aguarda medições; o Consumo mantém encerramento manual sem previsão inventada.
- O formulário remoto deixa de pedir tempo e envia testes solares/elétricos sem prazo, preservando a finalização opcional aos 100%.
- Testes com prazo de versões anteriores mantêm suas datas originais no histórico, sem alterar Room v11 ou backup JSON v9.

## 0.1.0-alpha.71

- Monitoramento remoto abre sem pop-up obrigatória: ações Ser monitorado e Monitorar outro celular ficam visíveis no topo do Labs.
- Aparelhos recentes permite reconectar sem novo QR enquanto a autorização do monitorado continuar válida; credenciais locais são protegidas pelo AndroidKeyStore.
- Carga automática inicia com um toque, sem prazo nem seleção de Solar/Elétrico, preservando a origem informada pelo Android sem atribuir a fonte incorretamente.
- Carga automática finaliza ao atingir 100% ou após três minutos consecutivos de desconexão confirmada, mantendo relatório e lacunas observacionais.
- Painel remoto destaca percentual, ganho na bateria e previsão, recolhendo temperaturas e outros detalhes técnicos sem ocultar os horários do teste.

## 0.1.0-alpha.70

- Labs mostra horário de início, término programado, duração transcorrida e tempo restante nos testes Solar, Elétrico e Consumo.
- O painel remoto recebe os horários reais do aparelho monitorado e diferencia prazo, tempo restante e fim efetivo.
- Testes de carga exibem previsão aproximada de 100% somente com ganho percentual e intervalos de carga suficientes.
- Testes manuais informam ausência de prazo, e relatórios e histórico passam a indicar o término programado.
- Novas verificações protegem horários, leituras antigas, lacunas e a separação entre previsão de carga e prazo do teste.

## 0.1.0-alpha.69

- Monitoramento remoto agora abre escolha entre ser monitorado e monitorar outro aparelho, com troca de função explícita.
- Leitor QR foi integrado à janela vertical do Labs, com permissão de câmera sob demanda e controle da lanterna.
- Controle LAN passa a iniciar testes solares e elétricos, com duração, condições e finalização opcional aos 100%.
- Aparelho monitorado identifica o nome informado pelo controlador, mostra notificação de acesso e permite revogar pela tela ou notificação.
- O novo pareamento usa QR temporário de uso único e sessão separada para reconexão; relatório permanece no aparelho medido.

## 0.1.0-alpha.68

- Manifesto declara a câmera opcional para o leitor de QR Code do Labs, corrigindo o erro Android Lint do workflow 48.
- O aplicativo continua instalável em aparelhos sem câmera, sem impedir o monitoramento de bateria e os testes locais.
- Uma regressão automática verifica que as permissões de câmera e o hardware opcional permaneçam declarados corretamente.
- Compilação Debug e testes unitários da alpha.67 já estavam aprovados no workflow 48; esta release corrige a etapa de Lint.

## 0.1.0-alpha.67

- Labs Solar e Carregamento Elétrico recebem Finalizar ao atingir 100%, ligado por padrão e disponível mesmo na duração manual.
- Uma nova leitura válida de 100% encerra automaticamente o teste, preserva os valores observados e emite a notificação de conclusão.
- O histórico e o relatório distinguem término por bateria cheia, por prazo e por ação manual, sem desligar fisicamente o carregador.
- Room v11 e backup JSON v9 preservam a escolha individual do teste, com migração que não ativa a função em registros antigos.

## 0.1.0-alpha.66

- Corrigida a criação do teste Android de Carregamento Elétrico: duração alvo e prazo final agora são informados ao LabTest.
- Adicionada regressão que bloqueia builds quando faltarem os parâmetros obrigatórios no teste instrumentado.
- Correção baseada no workflow 46: o aplicativo Debug compilou, mas a compilação dos testes Android falhou; medições e banco foram preservados.

## 0.1.0-alpha.65

- Labs recebe o Teste de Carregamento Elétrico com duração manual ou programada e verificação da fonte conectada informada pelo Android.
- O carregamento elétrico registra bateria inicial e final, tempo efetivo carregando, corrente líquida média e lacunas observacionais.
- Temperaturas inicial, média aritmética de leituras válidas, final e máxima ficam no relatório e histórico do Labs.
- Room v10 migra a tabela Labs preservando resultados anteriores sem inventar médias térmicas retroativas.
- Backup JSON v8 preserva a média térmica e a origem elétrica, mantendo leitura de backups antigos.

## 0.1.0-alpha.64

- Corrigida colisão entre o comando JSON e o método de envio, que impediria compilar a inicialização solar remota.
- Corrigido o teste de Novidades para reconhecer a versão anterior sem depender de uma data fixa no CHANGELOG.
- O contrato de integração AIDL permanece protegido enquanto a verificação diferencia a comunicação LAN autorizada.
- Pareamento remoto agora aceita somente IPv4 privado e o servidor escuta apenas na interface Wi-Fi selecionada.
- Conexões LAN têm prazo máximo de tentativa e leituras remotas antigas não são apresentadas como atuais.
- A tela remota identifica medições reais, bloqueia comandos quando desconectada e solicita monitoramento no celular A.

## 0.1.0-alpha.63

- Labs ganha Monitoramento remoto com geração de QR Code no celular monitorado e leitura no celular controlador.
- A conexão funciona pela mesma rede Wi‑Fi local, sem internet, exibindo bateria, corrente, temperatura e tempo do teste remoto.
- O controlador pode iniciar e finalizar remotamente o teste solar com configuração rápida de céu, painel e duração.
- A interface do Monitoramento remoto adota o layout em dois cartões Celular A/Celular B e o painel compacto de acompanhamento pedido para a beta.
- A release adiciona permissões e dependências locais para QR Code, câmera e comunicação LAN do recurso remoto.

## 0.1.0-alpha.62

- Home e Sessões agora calculam as médias históricas usando somente sessões finalizadas, sem misturar a sessão ativa.
- Transições de carga e descarga não somam o intervalo anterior na duração da nova sessão.
- Agrupamentos de sessões somam apenas minutos observados, mostram lacunas separadas e recusam sequências inconsistentes.
- Contagem de recargas por tipo de fonte acompanha a janela histórica realmente usada na previsão.
- Testes numéricos cobrem somas de duração, variações percentuais, médias ponderadas e trocas de estado.

## 0.1.0-alpha.61

- Corrigido o teste instrumentado de migração 8→9: agora cria corretamente as colunas legadas do Labs.
- Adicionado teste Android para comprovar o pico térmico persistido em sessões de carga e descarga sem amostras brutas.
- Captura PNG e exportação de sessões impedem operações concorrentes e desativam seus botões durante o processamento.
- Relatório CSV agora inclui também datas e horários locais legíveis, além dos timestamps originais.

## 0.1.0-alpha.60

- Temperatura máxima agora é registrada a cada leitura válida nas sessões, mesmo após descartar amostras antigas.
- Exportação CSV identifica se o pico veio do registro contínuo ou somente das amostras históricas retidas.
- Capturas PNG liberam a memória mesmo em caso de erro; exportação e ícone de câmera foram revisados.
- Migração Room 8→9 preserva os dados anteriores e o backup JSON v7 mantém os novos picos sem inventar valores antigos.
- Corrigida a inicialização compartilhada do banco usada pela exportação; pico térmico aparece também no cartão da sessão.

## 0.1.0-alpha.59

- Sessões exibem a temperatura máxima observada nas amostras disponíveis de carga e descarga.
- Um botão de câmera no topo de todas as telas permite salvar uma captura PNG.
- A aba Sessões permite exportar relatórios CSV com temperaturas e dados registrados.
- Room v8, backup JSON v6 e AIDL v1 preservados; máximo limitado às amostras retidas.

## 0.1.0-alpha.58

- A tela Novidades passa a exigir correspondência também com as notas de release da versão atual.
- O build rejeita notas de release desatualizadas ou sem as novidades anunciadas.
- Testes de regressão cobrem a ausência de itens e versões divergentes nas notas de release.

## Alpha.54

- Tela Novidades exclusiva do build, abertura automática por versão e verificação obrigatória em preBuild e CI.

# Pulso Bateria 0.1.0-alpha.57

## Novidades exclusivas da alpha.57

- Validação de correspondência entre Novidades e CHANGELOG da versão atual, com teste negativo.



## Novidade da alpha.50

- Labs compara testes com avisos explícitos de limitações observacionais; sem alterar banco ou estimativas.

## Novidade da alpha.49

- Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento.

- Barras de duração proporcional na comparação solar, com segmentos carregando, sem carga e sem leitura; nenhum gráfico de corrente sem amostras temporais.

Comparação A/B de dois testes solares concluídos no Labs, com variação de bateria, tempo de carga confirmado, corrente média válida e cobertura. A comparação é descritiva, sem atribuir causalidade ao painel nem preencher lacunas.


Novidade: indicação de evidência da previsão de carga/descarga, sem porcentagens artificiais de confiança.


Diagnóstico: classificação da fonte da corrente e aviso de consulta antiga, sem mudar o cálculo.

Auditoria de consistência de release e correção do CHANGELOG histórico. Sem mudanças funcionais.

- Proteção estática contra regressões das anotações Compose do workflow 35.
- Correção do histórico de versões no CHANGELOG.

## Resumo

- Reforço da validação do código e sincronização de versão no AGENTS.md.
- Teste de política de integração Labs incluído no workflow.
- Não altera o comportamento do aplicativo em execução.


- Tela inicial com cartão de recarga ajustado: **Carga média 0–100%**.
- Temperatura com classificação visual na Home: Dentro do padrão, Aquecida, Elevada e Alerta térmico.
- Faixas térmicas ancoradas no limite configurado em Ajustes, para melhor adaptação entre aparelhos.
- Sem mudanças no banco, backup, Labs ou API AIDL.

APK esperado do GitHub Actions: `Pulso-Bateria-v0.1.0-alpha.57-debug.apk`. ZIP contém código-fonte, não APK.

Diagnóstico: idade da consulta de bateria visível em Configurações > Fonte de medição > Dados técnicos.

Alpha.53: Labs economiza atualizações em segundo plano com coleta lifecycle-aware e relógio visual suspenso fora da tela.

Alpha.57: Novidades exclusivas do build, sem reutilização literal de itens antigos do CHANGELOG; teste de regressão do bloqueio. Próxima alpha.58 / 58.
