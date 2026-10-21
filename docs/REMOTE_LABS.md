# Monitoramento remoto LAN v2.1 — alpha.71

## Como usar

1. Instale a **mesma alpha.71** nos dois celulares. O formato de QR da alpha.68 (protocolo v1) **não é compatível** com o v2.
2. Conecte ambos à mesma rede Wi‑Fi ou a um hotspot em que clientes se enxerguem (alguns roteadores bloqueiam dispositivos entre si).
3. Na página **Labs → Monitoramento remoto**, escolha no topo **Ser monitorado** ou **Monitorar outro celular**. A página não exige escolher um papel em pop-up.
4. No celular que ficará carregando, selecione **Ser monitorado**. O aplicativo solicita monitoramento contínuo para que a leitura e o teste possam ocorrer com a tela apagada. Permita notificações quando solicitado.
5. Gere e mostre o QR Code (válido por 5 min, um pareamento). No outro celular, abra **Labs → Monitoramento remoto → Monitorar outro celular** e escaneie pela janela vertical.
6. No controlador, use **Iniciar automático até 100%** (sem configuração) ou **Configurar teste manual** para Solar/Elétrico, duração e **Finalizar ao atingir 100%**.
7. Enquanto o controlador estiver conectado, o monitorado mostra nome **declarado** pelo controlador e uma notificação com ação **Encerrar acesso**. A própria tela também possui este comando.
8. O painel remoto apresenta início, fim programado, duração, tempo restante e eventual estimativa de 100%; essa estimativa é calculada no aparelho monitorado com leituras válidas e não substitui o prazo configurado.
9. O teste, leituras e relatórios ficam **no aparelho monitorado**. Ao finalizar, o controlador acompanha o último relatório compartilhado; a comunicação perdida nunca cria intervalos de medição falsos.

## Segurança e limitações

- O QR contém IP privado, porta e um segredo temporário de pareamento. Após o primeiro pareamento o QR perde a validade; comandos usam um segredo de sessão diferente. Apenas uma sessão pode estar autorizada por vez.
- Revogar pela notificação ou pela tela invalida a sessão no monitorado; **não encerra o teste local nem corta a energia física**. Gerar outro QR também revoga a sessão anterior.
- Nome do controlador vem do modelo informado pelo próprio aplicativo remoto; **não é autenticação de identidade**.
- O transporte é **TCP local sem TLS**. Use somente redes Wi‑Fi confiáveis; quem controla o roteador pode observar tráfego de rede. Não expor a porta à internet nem usar redes públicas não confiáveis.
- A reconexão é tentada automaticamente enquanto o aplicativo do controlador permanecer aberto e a sessão não for revogada. Se o IP do monitorado mudar, o pareamento deverá ser refeito.
- O Android pode suspender processos, limitar conexões ou impedir início de foreground service em segundo plano. O serviço de monitoramento deve estar ativo antes de iniciar um teste remoto.
- A notificação depende das permissões e restrições do sistema; o controle de revogação pela **tela** permanece disponível.
- QR integrado usa câmera apenas enquanto a janela de leitura estiver aberta. Não impede o funcionamento de celulares sem câmera nas demais funções.
- Corrente e temperatura dependem de fontes Android e podem ser indisponíveis ou imprecisas; as médias usam apenas leituras válidas. Fonte USB não comprova energia elétrica de tomada.
- Antes da beta, validar em **dois aparelhos físicos**: QR em retrato, permissão de câmera negada/aprovada, Wi‑Fi e hotspot, revogação via notificação, reconexão e ambos os testes ao atingir 100%.

## Novidades da alpha.71

- **Aparelhos recentes:** até cinco entradas no controlador, para reconexão sem escanear novamente enquanto o host mantiver a mesma autorização. O acesso permanece limitado a um controlador, e o monitorado pode revogar qualquer acesso.
- **Segurança das credenciais:** somente o segredo de sessão aceito (não o token QR) é armazenado localmente cifrado via AES-GCM/AndroidKeyStore. O app desativa backup; não reutilizar credencial inválida. Se o host encerrar, reiniciar ou revogar, **será necessário novo QR**; IP ou porta alterados também invalidam a reconexão.
- **Desconectar** volta à lista local sem revogar a sessão no host, permitindo reconectar; **Esquecer** apaga a credencial deste controlador. **Encerrar acesso** no monitorado revoga a sessão.
- **Modo automático:** botão direto no controlador, sem escolher duração, Solar ou Elétrico. O monitorado precisa estar conectado a uma fonte, com leitura recente e monitoramento contínuo ativo. Se iniciar já a 100%, o teste é recusado. A origem é declarada conforme o Android, sem deduzir tomada ou painel.
- **Fim automático:** nova leitura de 100% ou 3 minutos consecutivos de leituras sem fonte, após carga efetivamente constatada, respeitando lacunas máximas de 90 segundos entre amostras. Caso uma leitura falte, o relógio de desconexão reinicia; encerramento por desconexão pode atrasar se a coleta for interrompida. Também é possível finalizar manualmente.
- **Interface:** bateria atual, ganho em pontos percentuais e previsão ganham destaque. Temperaturas e intervalos ficam recolhíveis. A projeção para 100% só aparece com evidência suficiente.
- **Persistência:** o histórico e as condições finais de AUTO usam Room v11 e backup JSON v9 sem nova migração. Não habilita serviços extras e não desativa regras de economia do Android.
