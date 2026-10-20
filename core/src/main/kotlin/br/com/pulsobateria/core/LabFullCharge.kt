package br.com.pulsobateria.core

/**
 * Encerramento por bateria cheia depende de uma amostra NOVA do Android,
 * posterior ao início do teste. Não interpreta FULL isoladamente como 100%.
 * O modo de consumo jamais é encerrado por esta regra.
 */
object LabFullCharge {
 fun shouldStop(
  kind: String,
  enabled: Boolean,
  percent: Int?,
  sampleTimeMs: Long,
  testStartedAtMs: Long,
  previousSampleTimeMs: Long?,
 ): Boolean = enabled && kind in setOf("SOLAR", "ELECTRIC") && percent == 100 &&
  sampleTimeMs > testStartedAtMs && sampleTimeMs > (previousSampleTimeMs ?: testStartedAtMs)
}
