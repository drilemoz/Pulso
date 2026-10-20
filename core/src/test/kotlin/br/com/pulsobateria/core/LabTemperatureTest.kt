package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class LabTemperatureTest {
 @Test fun meanUsesOnlyValidActualReadings() {
  val a = LabTemperature.add(0.0, 0, 41.6)
  val b = LabTemperature.add(a.sumC, a.count, 38.4)
  val c = LabTemperature.add(b.sumC, b.count, null)
  val d = LabTemperature.add(c.sumC, c.count, Double.NaN)
  val e = LabTemperature.add(d.sumC, d.count, 33.0)
  assertEquals(3, e.count)
  assertEquals((41.6+38.4+33.0)/3, e.meanC()!!, 0.000001)
 }
 @Test fun emptyIsUnknownNotZero() { assertNull(LabTemperature.meanC(0.0, 0)) }
 @Test fun rejectInvalidTemperature() {
  assertNull(LabTemperature.valid(-41.0))
  assertNull(LabTemperature.valid(101.0))
  assertNull(LabTemperature.valid(Double.POSITIVE_INFINITY))
  assertEquals(40.0, LabTemperature.valid(40.0)!!, 0.0)
 }
 @Test fun restoredLegacyEmptyStaysUnknown() {
  assertEquals(0, LabTemperature.add(0.0, 0, null).count)
  assertNull(LabTemperature.meanC(0.0, 0))
 }
}
