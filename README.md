# Pulso Bateria

**0.1.0-alpha.65 • versionCode 65** — Android nativo, Kotlin e Jetpack Compose.

## Novidades da alpha.65

- Labs recebe o Teste de Carregamento Elétrico com duração manual ou programada e verificação da fonte conectada informada pelo Android.
- O carregamento elétrico registra bateria inicial e final, tempo efetivo carregando, corrente líquida média e lacunas observacionais.
- Temperaturas inicial, média aritmética de leituras válidas, final e máxima ficam no relatório e histórico do Labs.
- Room v10 migra a tabela Labs preservando resultados anteriores sem inventar médias térmicas retroativas.
- Backup JSON v8 preserva a média térmica e a origem elétrica, mantendo leitura de backups antigos.

## Novidades da alpha.64

- Corrigida colisão entre o comando JSON e o método de envio, que impediria compilar a inicialização solar remota.
- Corrigido o teste de Novidades para reconhecer a versão anterior sem depender de uma data fixa no CHANGELOG.
- O contrato de integração AIDL permanece protegido enquanto a verificação diferencia a comunicação LAN autorizada.
- Pareamento remoto agora aceita somente IPv4 privado e o servidor escuta apenas na interface Wi-Fi selecionada.
- Conexões LAN têm prazo máximo de tentativa e leituras remotas antigas não são apresentadas como atuais.
- A tela remota identifica medições reais, bloqueia comandos quando desconectada e solicita monitoramento no celular A.

## Novidades da alpha.63

- Labs ganha Monitoramento remoto com geração de QR Code no celular monitorado e leitura no celular controlador.
- A conexão funciona pela mesma rede Wi‑Fi local, sem internet, exibindo bateria, corrente, temperatura e tempo do teste remoto.
- O controlador pode iniciar e finalizar remotamente o teste solar com configuração rápida de céu, painel e duração.
- A interface do Monitoramento remoto adota o layout em dois cartões Celular A/Celular B e o painel compacto de acompanhamento pedido para a beta.
- A release adiciona permissões e dependências locais para QR Code, câmera e comunicação LAN do recurso remoto.

## Novidades da alpha.62

- Home e Sessões agora calculam as médias históricas usando somente sessões finalizadas, sem misturar a sessão ativa.
- Transições de carga e descarga não somam o intervalo anterior na duração da nova sessão.
- Agrupamentos de sessões somam apenas minutos observados, mostram lacunas separadas e recusam sequências inconsistentes.
- Contagem de recargas por tipo de fonte acompanha a janela histórica realmente usada na previsão.
- Testes numéricos cobrem somas de duração, variações percentuais, médias ponderadas e trocas de estado.

## Novidades da alpha.61

- Corrigido o teste instrumentado de migração 8→9: agora cria corretamente as colunas legadas do Labs.
- Adicionado teste Android para comprovar o pico térmico persistido em sessões de carga e descarga sem amostras brutas.
- Captura PNG e exportação de sessões impedem operações concorrentes e desativam seus botões durante o processamento.
- Relatório CSV agora inclui também datas e horários locais legíveis, além dos timestamps originais.

## Novidades da alpha.60

- Temperatura máxima agora é registrada a cada leitura válida nas sessões, mesmo após descartar amostras antigas.
- Exportação CSV identifica se o pico veio do registro contínuo ou somente das amostras históricas retidas.
- Capturas PNG liberam a memória mesmo em caso de erro; exportação e ícone de câmera foram revisados.
- Migração Room 8→9 preserva os dados anteriores e o backup JSON v7 mantém os novos picos sem inventar valores antigos.
- Corrigida a inicialização compartilhada do banco usada pela exportação; pico térmico aparece também no cartão da sessão.

## Novidades da alpha.59

- Sessões exibem a temperatura máxima observada nas amostras disponíveis de carga e descarga.
- Um botão de câmera no topo de todas as telas permite salvar uma captura PNG.
- A aba Sessões permite exportar relatórios CSV com temperaturas e dados registrados.

## Novidades da alpha.58

- Novidades e RELEASE_NOTES sincronizados e bloqueio automático em caso de divergência.
- Dois cenários extras de regressão do gate de release.

## Novidades da alpha.57

- Correspondência obrigatória entre itens da tela Novidades e o CHANGELOG da versão atual.
- Novo cenário de regressão para notas divergentes.

## Novidades da alpha.55

- Testes negativos e validação mais rigorosa da tela de Novidades no build.

## Novidades da alpha.54

- Tela Novidades por build com abertura automática e acesso manual. Build bloqueado se conteúdo não for atualizado.

## Novidades da alpha.50

- Comparação solar sinaliza cobertura inferior a 80%, duração diferente, dados de bateria ou corrente indisponíveis. Critérios conservadores, sem pontuação de precisão artificial.

## Novidades da alpha.49

- Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento.

- Barras de duração proporcional na comparação solar, com segmentos carregando, sem carga e sem leitura; nenhum gráfico de corrente sem amostras temporais.

- Comparação de dois testes solares concluídos no Labs, com seleção A/B, variação em pontos percentuais, carga confirmada, corrente média e cobertura. Sem dados fabricados nem mudanças de banco.

## Novidades da alpha.46

- A previsão agora informa a evidência disponível (tendência atual, histórico limitado ou múltiplas sessões). Não apresenta porcentagem de confiança artificial.
- Cálculo do tempo estimado permanece inalterado.

## Novidades da alpha.45

- Diagnóstico de corrente com tipo da fonte e aviso de consulta antiga (>60 segundos).
- A classificação não certifica atualização interna do sensor nem altera as medições.

## Novidades da alpha.39 — Tela inicial térmica e cartão de recarga ajustado

- Encurta o título do cartão histórico de recarga para **Carga média 0–100%**, evitando quebra de linha ruim na tela inicial.
- Adiciona classificação visual da temperatura na tela inicial com quatro faixas: **Dentro do padrão**, **Aquecida**, **Elevada** e **Alerta térmico**.
- As faixas usam como referência o limite térmico configurado em Ajustes, adaptando melhor o app a aparelhos que costumam trabalhar mais quentes sem esconder risco real.
- Mantém intactas as médias históricas, a API multiaplicativos dos Labs, o agrupamento de sessões em até 5 minutos e o banco Room v8.
- A compilação Android da alpha.39 continua pendente até executar o GitHub Actions.

## Projeto

Aplicativo offline para monitoramento de bateria com dados oficiais do Android, histórico local, notificações e ferramentas de laboratório.

Alpha.53: Labs observa dados apenas com a interface ativa e suspende ticks visuais fora da tela. O serviço de monitoramento não foi alterado.

Alpha.57: Novidades exclusivas do build, sem reutilização literal de itens antigos do CHANGELOG; teste de regressão do bloqueio. Próxima alpha.58 / 58.
