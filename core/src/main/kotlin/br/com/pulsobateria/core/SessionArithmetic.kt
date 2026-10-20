package br.com.pulsobateria.core

/** Um resumo independente por sessão, sem reconstruir intervalos entre registros Room. */
data class ObservedSessionPart(
 val mode: Mode, val startedAtMs: Long, val endedAtMs: Long,
 val initialPercent: Int, val finalPercent: Int,
 val observedDurationMs: Long, val samples: Int
)

data class GroupedSessionMetrics(
 val observedDurationMs: Long, val unobservedBetweenMs: Long,
 val samples: Int, val initialPercent: Int, val finalPercent: Int,
 val ratePercentHour: Double?
)

object SessionArithmetic {
 /**
  * Soma apenas as durações declaradas e verificadas dos registros originais.
  * A lacuna entre sessões é reportada à parte e jamais entra na velocidade média.
  * Se a sequência tiver transição incoerente, não se deve exibir uma soma agrupada.
  */
 fun group(parts: List<ObservedSessionPart>): GroupedSessionMetrics? {
  val first = parts.firstOrNull() ?: return null
  if (first.mode !in setOf(Mode.CHARGING, Mode.DISCHARGING)) return null
  var observed = 0L
  var unobserved = 0L
  var samples = 0L
  for ((index, current) in parts.withIndex()) {
   if (current.mode != first.mode || current.startedAtMs < 0L ||
    current.endedAtMs < current.startedAtMs ||
    current.initialPercent !in 0..100 || current.finalPercent !in 0..100 ||
    current.observedDurationMs < 0 || current.samples < 0 ||
    current.observedDurationMs > current.endedAtMs-current.startedAtMs+60_000L ||
    observed > Long.MAX_VALUE-current.observedDurationMs ||
    samples > Int.MAX_VALUE.toLong()-current.samples) return null
   if (index > 0) {
    val previous = parts[index-1]
    val gap = current.startedAtMs-previous.endedAtMs
    if (gap !in 0L..300_000L || previous.finalPercent != current.initialPercent ||
     unobserved > Long.MAX_VALUE-gap) return null
    unobserved += gap
   }
   observed += current.observedDurationMs
   samples += current.samples
  }
  val last = parts.last()
  // O grupo segue uma única direção; recalibrações/variações contrárias não
  // devem produzir um ritmo aparentemente confiável.
  val directionValid = parts.all {
   when (first.mode) {
    Mode.CHARGING -> it.finalPercent >= it.initialPercent
    Mode.DISCHARGING -> it.finalPercent <= it.initialPercent
    else -> false
   }
  }
  val rate = if(directionValid) BatteryMath.sessionRate(
   first.initialPercent,last.finalPercent,observed,samples.toInt()) else null
  return GroupedSessionMetrics(observed,unobserved,samples.toInt(),
   first.initialPercent,last.finalPercent,rate)
 }
}
