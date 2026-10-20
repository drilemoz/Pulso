# Auditoria pré-beta | Pulso Bateria 0.1.0-alpha.62

Data: 20/10/2026. Escopo: fórmulas, conversões, filtros e somatórios do código-fonte alpha.61, corrigidos quando necessário na alpha.62. Não é laudo metrológico e não substitui instrumentação Android.

## Home e Sessões: o que deve ser igual

- A **média histórica equivalente a 100%** na Home e em Sessões usa **somente sessões finalizadas válidas** (durações observadas e variações verificadas). A alpha.61 misturava sessões abertas na Home, mas não em Sessões. **Corrigido na alpha.62.**
- **Descarga (pontos percentuais/h):** soma das perdas válidas ÷ soma dos tempos observados em horas. Equivalente de 100%: `100 ÷ taxa` horas.
- **Carga (pontos percentuais/h):** soma dos ganhos válidos ÷ soma dos tempos observados em horas. Equivalente 0–100%: `100 ÷ taxa` horas.
- Nenhum total de percentuais equivale a mAh; por exemplo, 15 pontos percentuais perdidos NÃO são 15% da capacidade nominal necessariamente medida.

### Exemplo reprodutível

Duas descargas finalizadas: 80→70% em 60 minutos, depois 70→65% em 30 minutos, com 3 minutos sem leituras entre elas. O grupo mostra **90 minutos observados**, **3 minutos sem observação**, **15 pontos perdidos** e **10 pontos/h**. Na projeção histórica equivalente, `100 ÷ 10 = 10 h`. Usar 93 minutos reduziria falsamente a taxa. O exemplo passou na auditoria Kotlin/JVM.

## Por que o horário de Home pode divergir do histórico em Sessões

O cartão de **horário previsto** na Home é dinâmico. Quando há dados, mistura 70% da taxa **local recente** e 30% da taxa **histórica**; ao carregar, pode usar o histórico da fonte detectada ou uma faixa 80–100% observada. Já o quadro histórico em Sessões é uma taxa global extrapolada de sessões finalizadas. Assim, os números das **médias históricas** devem coincidir, mas o horário previsto de conclusão/autonomia **não é obrigado a coincidir** com o equivalente médio de um ciclo de 100%.

## Duração e agrupamentos

A alpha.61 podia incorporar à primeira leitura de um novo modo de carga/descarga um trecho desde a última leitura do modo anterior. **Corrigido:** o relógio de intervalo é reiniciado na transição ou após lacuna que exige outra sessão. A função `SessionArithmetic.group` valida sequência, fronteira dos níveis e durações; entradas inválidas ficam como cartões individuais. Nenhuma lacuna recebe minutos artificiais.

## Corrente, energia e temperatura

- `CURRENT_NOW` e `CURRENT_AVERAGE`: Android declara µA; valor em mA é `µA/1.000`, com sinal de entrada/saída, disponibilidade e correção OEM apenas quando sustentada pelas verificações existentes.
- `CHARGE_COUNTER`: Android declara µAh; diferença ao longo de tempo medido (ms) produz média líquida em mA por `ΔµAh × 3.600 / Δt_ms`. O sinal não é reescrito para combinar com o estado.
- Estimativa percentual com capacidade informada é rotulada **estimativa**; não substitui sensor, não identifica corrente do cabo e não autoriza calcular potência da fonte.
- Temperatura Android em décimos de grau: dividir por 10. O máximo persistido por sessão representa somente leituras válidas recebidas, não períodos sem monitoramento nem picos anteriores à migração.

## Auditoria e limites

- 16 scripts `tools/test_*.py`: aprovados, incluindo 16 testes SQLite e 9 cenários de bloqueio de Novidades.
- 232 testes Kotlin/JVM de produção/testes existentes e novos executados com compilador local 1.9 e um executor leve de asserções compatíveis com JUnit: **232 aprovados**. Esse executor local não é o JUnit/Gradle oficial.
- Teste independente `tools/run_math_audit.sh` e 16 invariantes externos Kotlin/JVM: aprovados.
- `tools/validate_source.py`, `tools/check_release_news.py`: aprovados.
- Tentativa de `:core:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --offline`: **não iniciou**, porque o Wrapper tentou obter a distribuição do Gradle em `services.gradle.org` e o host não estava acessível.
- Room v9, backup JSON v7 e contrato AIDL v1 não foram alterados nesta atualização.

## Portão para beta

Antes de alterar o número para beta: executar **o workflow CI de compilação e testes JUnit reais**, executar migrações Room instrumentadas, instalar e testar em Android real, comparar sessão de carga/descarga contra percentual/tempo do sistema, exportar e restaurar backup e confirmar que Home e Sessões usam o mesmo histórico fechado. Se houver erro de build ou campo divergente, corrigir em nova alpha antes da beta. Sem evidência de aparelho, não afirmar que todos os valores físicos são exatos.
