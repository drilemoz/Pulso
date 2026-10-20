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
