package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class SessionThermalPeakTest {
 @Test fun retainedMaximumSurvivesLowerReadings() {
  assertEquals(41.5, SessionThermalPeak.update(null,41.5)!!,0.0)
  assertEquals(41.5, SessionThermalPeak.update(41.5,32.0)!!,0.0)
  assertEquals(42.0, SessionThermalPeak.update(41.5,42.0)!!,0.0)
 }
 @Test fun invalidReadingsDoNotInventPeaks() {
  assertNull(SessionThermalPeak.update(null,Double.NaN))
  assertNull(SessionThermalPeak.update(null,Double.POSITIVE_INFINITY))
  assertNull(SessionThermalPeak.update(null,101.0))
  assertNull(SessionThermalPeak.update(null,-41.0))
  assertEquals(37.0,SessionThermalPeak.update(37.0,Double.NaN)!!,0.0)
  assertEquals(36.0,SessionThermalPeak.update(Double.NaN,36.0)!!,0.0)
 }
}
