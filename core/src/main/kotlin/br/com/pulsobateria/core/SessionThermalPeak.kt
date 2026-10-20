package br.com.pulsobateria.core

/** Mantém o maior valor confiável observado, mesmo que o histórico bruto seja podado. */
object SessionThermalPeak {
 fun update(previous: Double?, reading: Double?): Double? {
  val old = previous?.takeIf { it.isFinite() && it in -40.0..100.0 }
  val next = reading?.takeIf { it.isFinite() && it in -40.0..100.0 }
  return when { old == null -> next; next == null -> old; next > old -> next; else -> old }
 }
}
