package br.com.pulsobateria.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ThermalBandsTest {
 @Test fun defaultThresholdProducesExpectedBands() {
  val bands = ThermalBands.bounds(43)
  assertEquals(38, bands.normalMaxC)
  assertEquals(41, bands.warmMaxC)
  assertEquals(42, bands.highMinC)
  assertEquals(42, bands.highMaxC)
  assertEquals(43, bands.alertMinC)
 }

 @Test fun deviceCanUseHigherAlertThreshold() {
  val bands = ThermalBands.bounds(45)
  assertEquals(40, bands.normalMaxC)
  assertEquals(43, bands.warmMaxC)
  assertEquals(44, bands.highMinC)
  assertEquals(44, bands.highMaxC)
  assertEquals(45, bands.alertMinC)
 }

 @Test fun classificationsRespectConfiguredThreshold() {
  assertEquals(ThermalLevel.NORMAL, ThermalBands.classify(37.9, 43))
  assertEquals(ThermalLevel.WARM, ThermalBands.classify(40.8, 43))
  assertEquals(ThermalLevel.HIGH, ThermalBands.classify(42.0, 43))
  assertEquals(ThermalLevel.ALERT, ThermalBands.classify(43.0, 43))
  assertEquals(ThermalLevel.WARM, ThermalBands.classify(42.0, 45))
 }

 @Test fun invalidTemperatureIsUnknown() {
  assertEquals(ThermalLevel.UNKNOWN, ThermalBands.classify(null, 43))
  assertEquals(ThermalLevel.UNKNOWN, ThermalBands.classify(Double.NaN, 43))
 }
}
