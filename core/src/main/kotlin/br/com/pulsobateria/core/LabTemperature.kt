package br.com.pulsobateria.core

/** Média ARITMÉTICA de leituras térmicas válidas, sem interpolar lacunas. */
object LabTemperature {
 data class Aggregate(val sumC: Double = 0.0, val count: Int = 0) {
  fun meanC(): Double? = if (count > 0 && sumC.isFinite() && sumC / count in -40.0..100.0)
   sumC / count else null
 }
 fun valid(value: Double?): Double? = value?.takeIf { it.isFinite() && it in -40.0..100.0 }
 fun add(sumC: Double, count: Int, sample: Double?): Aggregate {
  val safeSample = valid(sample)
  if (safeSample == null || count !in 0 until 1_000_000 || !sumC.isFinite()) return Aggregate(sumC, count)
  return Aggregate(sumC + safeSample, count + 1)
 }
 fun meanC(sumC: Double, count: Int): Double? = Aggregate(sumC, count).meanC()
}
