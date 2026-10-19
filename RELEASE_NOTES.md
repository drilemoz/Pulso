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
