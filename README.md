# Pulso Bateria

**0.1.0-alpha.39 • versionCode 39** — Android nativo, Kotlin e Jetpack Compose.

## Novidades da alpha.39 — Tela inicial térmica e cartão de recarga ajustado

- Encurta o título do cartão histórico de recarga para **Carga média 0–100%**, evitando quebra de linha ruim na tela inicial.
- Adiciona classificação visual da temperatura na tela inicial com quatro faixas: **Dentro do padrão**, **Aquecida**, **Elevada** e **Alerta térmico**.
- As faixas usam como referência o limite térmico configurado em Ajustes, adaptando melhor o app a aparelhos que costumam trabalhar mais quentes sem esconder risco real.
- Mantém intactas as médias históricas, a API multiaplicativos dos Labs, o agrupamento de sessões em até 5 minutos e o banco Room v8.
- A compilação Android da alpha.39 continua pendente até executar o GitHub Actions.

## Projeto

Aplicativo offline para monitoramento de bateria com dados oficiais do Android, histórico local, notificações e ferramentas de laboratório.
