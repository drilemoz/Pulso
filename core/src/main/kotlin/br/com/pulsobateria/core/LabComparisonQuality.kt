package br.com.pulsobateria.core

/** Auditoria conservadora da comparação de dois testes solares concluídos.
 * Não estima dados ausentes nem determina qual painel é mais eficiente.
 */
object LabComparisonQuality {
 data class TestSummary(val totalMs: Long, val chargingMs: Long, val notChargingMs: Long,
                        val initialPercent: Int?, val finalPercent: Int?, val meanMa: Double?)
 fun warnings(a: TestSummary, b: TestSummary): List<String> {
  val items = listOf(a,b)
  val warnings = mutableListOf<String>()
  if (items.any { it.totalMs <= 0L }) warnings += "Há teste sem duração válida."
  if (items.any { LabReportMath.coveragePercent(it.totalMs,it.chargingMs,it.notChargingMs) < 80 })
   warnings += "Pelo menos um teste tem cobertura inferior a 80%; compare com cautela."
  if (items.any { it.initialPercent !in 0..100 || it.finalPercent !in 0..100 })
   warnings += "Variação da bateria indisponível ou inválida em pelo menos um teste."
  if (items.any { it.meanMa == null || !it.meanMa.isFinite() || it.meanMa <= 0.0 })
   warnings += "Corrente média válida indisponível em pelo menos um teste."
  if (a.totalMs > 0 && b.totalMs > 0) {
   val min = minOf(a.totalMs,b.totalMs).toDouble()
   val max = maxOf(a.totalMs,b.totalMs).toDouble()
   if (max / min > 1.25) warnings += "As durações diferem mais de 25%; compare taxas e condições, não só variação total."
  }
  return warnings
 }
}
