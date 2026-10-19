package br.com.pulsobateria.core

enum class ThermalLevel { NORMAL, WARM, HIGH, ALERT, UNKNOWN }

data class ThermalBandBounds(
 val normalMaxC: Int,
 val warmMaxC: Int,
 val highMinC: Int,
 val highMaxC: Int,
 val alertMinC: Int,
)

object ThermalBands {
 fun bounds(hotAlertC: Int): ThermalBandBounds {
  val alert = hotAlertC.coerceIn(35, 60)
  val normalMax = (alert - 5).coerceAtLeast(28)
  val warmMax = (alert - 2).coerceAtLeast(normalMax + 1)
  val highMin = (warmMax + 1).coerceAtMost(alert - 1)
  val highMax = (alert - 1).coerceAtLeast(highMin)
  return ThermalBandBounds(
   normalMaxC = normalMax,
   warmMaxC = warmMax,
   highMinC = highMin,
   highMaxC = highMax,
   alertMinC = alert,
  )
 }

 fun classify(temperatureC: Double?, hotAlertC: Int): ThermalLevel {
  if (temperatureC == null || !temperatureC.isFinite()) return ThermalLevel.UNKNOWN
  val bands = bounds(hotAlertC)
  return when {
   temperatureC <= bands.normalMaxC.toDouble() -> ThermalLevel.NORMAL
   temperatureC <= bands.warmMaxC.toDouble() -> ThermalLevel.WARM
   temperatureC < bands.alertMinC.toDouble() -> ThermalLevel.HIGH
   else -> ThermalLevel.ALERT
  }
 }
}
