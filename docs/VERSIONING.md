# Versionamento

Versão atual: **0.1.0-alpha.67**, **versionCode 67**.

Cada alteração no projeto deve aumentar sequencialmente o `versionCode` e o `versionName`, além de sincronizar README, CHANGELOG, RELEASE_NOTES e documentação de validação.

Alpha.39: ajuste da Home com título de recarga encurtado e indicador térmico visual baseado no limite configurado pelo usuário. Próxima alpha.40 / 40.

Alpha.41: consistência de versão verificada automaticamente no AGENTS.md e teste de política AIDL/Labs no CI. Próxima alpha.43 / 43.

Alpha.42: teste estático de regressão Compose e correção de histórico do CHANGELOG. Próxima alpha.43 / 43.

Alpha.45: diagnóstico de origem e atualidade da consulta, sem alterar cálculos ou persistência. Próxima alpha.47 / 47.

Alpha.46: indicação de evidência de previsão, sem alteração de fórmulas. Próxima alpha.47 / 47.

Alpha.47: comparação A/B de testes solares concluídos; próxima alpha.50 / 50.

Alpha.48: Barras de duração proporcional na comparação solar, com segmentos carregando, sem carga e sem leitura; nenhum gráfico de corrente sem amostras temporais. Próxima alpha.49 / 49.

Alpha.49: Exportação de comparação A/B em CSV via área de transferência, com unidades no cabeçalho e campos ausentes vazios; sem permissão de armazenamento. Próxima alpha.50 / 50.

- Alpha.50: Labs sinaliza limitações da comparação solar com critérios conservadores (cobertura <80%, dados ausentes, duração >25% diferente); não inventar qualidade percentual nem modificar Room/backup. Próxima alpha.51 / 51.

Alpha.51: Labs: listas de testes concluídos e solares memorizadas por histórico, evitando refiltragem a cada atualização do cronômetro; resultados e persistência intactos.

Alpha.52: Histórico: agrupamento e títulos de datas passam a considerar explicitamente o locale no cache Compose; preserva sessões, filtros e lacunas observadas.

Alpha.53: coleta de estado Labs atrelada ao ciclo de vida; relógio apenas visual pausa fora da tela, retomando pelo horário real. Próxima alpha.54 / 54.

Alpha.55: Novidades com testes de falha obrigatória para versões divergentes e itens inválidos. Próxima alpha.57.

Alpha.57: Novidades exclusivas do build, sem reutilização literal de itens antigos do CHANGELOG; teste de regressão do bloqueio. Próxima alpha.58 / 58.

Alpha.58: RELEASE_NOTES sincronizado com Novidades e CHANGELOG; CI bloqueia divergências. Avaliar entrada na fase beta antes de novas versões.

Alpha.59: máximo de temperatura observada, captura de tela e exportação CSV das sessões.

Alpha.60: pico térmico persistido em Room v9; backup v7 com compatibilidade v1..7. Próxima alpha.61 / 61.

Alpha.61: teste Room 8→9 e pico térmico das duas modalidades, captura/CSV sem concorrência, horários locais no CSV; Room v9 e backup v7 mantidos. Próxima alpha.63 / 62.

Alpha.62: auditoria de totais Home/Sessões, exclusão da sessão aberta das médias históricas, correção do tempo em troca de modo, totalizador observacional para grupos e contagem de fonte usada. Próxima alpha.64 / 64.

Alpha.64: correção dos testes de Novidades e AIDL/LAN; pareamento privado, limites de conexão e leituras remotas recentes. Próxima alpha.65 / 65.

Alpha.65: teste de carregamento elétrico no Labs; média térmica persistida de leituras válidas. Room v10 com migração 9→10; backup JSON v8 compatível com versões anteriores. Próxima alpha.66 / 66.

Alpha.66: corrigida fixture Android de Carregamento Elétrico em `RoomMigrationTest` e ampliado contrato para impedir regressão. Sem migração nova, Room v10 e backup JSON v8 mantidos. Próxima alpha.67 / 67.

Alpha.67: Solar e carregamento elétrico podem finalizar automaticamente em uma leitura nova de 100%, com opção salva em Room v11 (10→11) e backup JSON v9. Próxima alpha.68 / 68.
