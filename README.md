# Pulso Bateria

**0.1.0-alpha.61 • versionCode 61** — Android nativo, Kotlin e Jetpack Compose.

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
