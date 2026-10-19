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
