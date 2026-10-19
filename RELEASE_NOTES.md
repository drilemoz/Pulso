# Pulso Bateria 0.1.0-alpha.46

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

APK esperado do GitHub Actions: `Pulso-Bateria-v0.1.0-alpha.46-debug.apk`. ZIP contém código-fonte, não APK.

Diagnóstico: idade da consulta de bateria visível em Configurações > Fonte de medição > Dados técnicos.
