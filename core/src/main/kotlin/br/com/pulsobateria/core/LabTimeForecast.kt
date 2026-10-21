package br.com.pulsobateria.core

/**
 * Estimativa estritamente observacional para testes de carga do Labs.
 * Não depende de mA, capacidade nominal, histórico de outros testes ou
 * de intervalos sem observação. Sem evidência suficiente, retorna null.
 */
object LabTimeForecast {
 private const val MIN_SPAN_MS = 5L * 60_000L
 private const val MIN_CHARGING_MS = 3L * 60_000L
 private const val MAX_READING_AGE_MS = 90_000L
 private const val MAX_ETA_MS = 7L * 24L * 60L * 60_000L

 fun fullAt(
  kind: String, running: Boolean, startedAtMs: Long, lastReadingAtMs: Long?,
  nowMs: Long, initialPercent: Int?, currentPercent: Int?,
  chargingMs: Long, lastMode: String?
 ): Long? {
  if (!running || kind !in setOf("SOLAR", "ELECTRIC") || lastMode != "CHARGING" ||
   startedAtMs <= 0 || nowMs < startedAtMs || lastReadingAtMs == null ||
   lastReadingAtMs !in startedAtMs..nowMs || nowMs-lastReadingAtMs > MAX_READING_AGE_MS ||
   initialPercent == null || currentPercent == null || initialPercent !in 0..98 ||
   currentPercent !in 1..99 || currentPercent <= initialPercent) return null
  val elapsed = lastReadingAtMs-startedAtMs
  val gained = currentPercent-initialPercent
  if (elapsed < MIN_SPAN_MS || gained < 2 || chargingMs < MIN_CHARGING_MS ||
   chargingMs > elapsed || chargingMs.toDouble()/elapsed < 0.75) return null
  val rate = gained * 3_600_000.0 / elapsed
  if (!rate.isFinite() || rate !in 0.1..200.0) return null
  val remaining = ((100-currentPercent) * 3_600_000.0 / rate)
  if (!remaining.isFinite() || remaining < 1.0 || remaining > MAX_ETA_MS ||
   lastReadingAtMs > Long.MAX_VALUE-remaining.toLong()) return null
  // Projeta a partir da última leitura, não do instante de abertura da tela.
  val result = lastReadingAtMs + remaining.toLong()
  return result.takeIf { it > nowMs }
 }
}
