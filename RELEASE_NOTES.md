# Pulso Bateria 0.1.0-alpha.37

- **Sessões mais compactas:** Comparar, Relatórios, Todas, Carga e Descarga na mesma linha; “Unir ≤5 min” e ajuda no cabeçalho.
- **Médias históricas visíveis:** autonomia equivalente para 100% e recarga equivalente 0–100%, calculadas apenas de sessões finalizadas válidas, sinalizando ausência de dados.
- Agrupamento visual aceita lacunas de no máximo cinco minutos com proteção de percentual, tipo e fonte; sem preencher tempo não observado ou fundir dados salvos.
- **API Labs para vários aplicativos (mantida da alpha.36):** autorize até 20 clientes instalados e controle cada permissão individualmente. Revogue um ou todos com confirmação.
- O cliente autorizado da alpha.35 é preservado, incluindo o último ID de teste (se assinatura válida). Cada cliente tem relatório próprio, sem acesso a testes alheios.
- API AIDL v1 compatível, com UID e SHA-256 validados por chamada, rejeição de UID compartilhado e operações serializadas.
- Um teste por vez; API exige monitoramento do Pulso já ativo. Não instala código no Vigia IA e não exige internet.
- Informação técnica: capacidade útil em mAh continua sendo fallback de corrente estimada por variação percentual/h; não altera leituras do Android.
- Barra inferior Início / Sessões / Labs / Config., histórico, Room v8 / backup JSON v6 preservados.

APK esperado do GitHub Actions: `Pulso-Bateria-v0.1.0-alpha.37-debug.apk`. ZIP contém código-fonte, não APK.
