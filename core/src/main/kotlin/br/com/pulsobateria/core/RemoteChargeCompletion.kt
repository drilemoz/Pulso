package br.com.pulsobateria.core

/**
 * Registra a transição realmente observada de teste remoto em execução -> finalizado.
 * Um status antigo recebido no primeiro contato não gera alerta. Uma mesma conclusão
 * não produz notificações repetidas enquanto o controlador consulta o servidor.
 * A chave é definida pelo chamador (credencial de sessão + ID persistido do Labs).
 */
class RemoteChargeCompletion {
 private val active = LinkedHashSet<String>()
 private val notified = LinkedHashSet<String>()

 @Synchronized
 fun observe(sessionKey: String, testId: Long?, kind: String?, running: Boolean, status: String?): String? {
  if (sessionKey.isBlank() || testId == null || testId <= 0L) return null
  val key = "$sessionKey:$testId"
  if (running) {
   if (kind in setOf("SOLAR", "ELECTRIC", "AUTO")) active.add(key)
   trim()
   return null
  }
  if (kind !in setOf("SOLAR", "ELECTRIC", "AUTO") || key !in active || key in notified) return null
  val message = when(status) {
   "FULL" -> "Bateria atingiu 100%. Teste encerrado e relatório salvo no celular monitorado."
   "UNPLUGGED" -> "Fonte desconectada. Teste encerrado e relatório salvo no celular monitorado."
   "COMPLETED" -> "Teste de carga concluído. Relatório salvo no celular monitorado."
   "STOPPED" -> "Teste de carga finalizado manualmente. Relatório salvo no celular monitorado."
   else -> null
  } ?: return null
  active.remove(key)
  notified.add(key)
  trim()
  return message
 }

 private fun trim() {
  while(active.size > 100) active.remove(active.first())
  while(notified.size > 100) notified.remove(notified.first())
 }
}
