package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class SessionArithmeticTest {
 private fun part(start: Long, end: Long, duration: Long, from: Int, to: Int,
  mode: Mode = Mode.DISCHARGING, count: Int = 12) =
  ObservedSessionPart(mode,start,end,from,to,duration,count)

 @Test fun dischargeSumsOnlyObservedTimeAndKeepsGapSeparate() {
  // 80→70 em 60 minutos + 70→65 em 30 minutos; intervalo sem leitura de 3 min.
  val a=part(1L,3_600_001L,3_600_000L,80,70)
  val b=part(3_780_001L,5_580_001L,1_800_000L,70,65)
  val total=SessionArithmetic.group(listOf(a,b))!!
  assertEquals(5_400_000L,total.observedDurationMs)
  assertEquals(180_000L,total.unobservedBetweenMs)
  assertEquals(24,total.samples)
  assertEquals(15,total.initialPercent-total.finalPercent)
  assertEquals(-10.0,total.ratePercentHour!!,0.0001)
  assertEquals(BatteryMath.sessionRate(80,65,5_400_000L,24),total.ratePercentHour)
 }

 @Test fun chargingAddsPartialSessionsWithoutDoubleCountingTheBoundary() {
  val a=part(0,7_200_000L,7_200_000L,30,50,Mode.CHARGING)
  val b=part(7_200_000L,10_800_000L,3_600_000L,50,60,Mode.CHARGING)
  val total=SessionArithmetic.group(listOf(a,b))!!
  assertEquals(10_800_000L,total.observedDurationMs)
  assertEquals(0L,total.unobservedBetweenMs)
  assertEquals(30,total.finalPercent-total.initialPercent)
  assertEquals(10.0,total.ratePercentHour!!,0.001)
 }

 @Test fun invalidOrMismatchedGroupsCannotProduceMisleadingSums() {
  val a=part(0,3_600_000L,3_600_000L,80,70)
  assertNull(SessionArithmetic.group(emptyList()))
  assertNull(SessionArithmetic.group(listOf(a,a.copy(startedAtMs=3_600_000L,endedAtMs=4_000_000L,initialPercent=71))))
  assertNull(SessionArithmetic.group(listOf(a,a.copy(startedAtMs=3_600_000L,endedAtMs=4_000_000L,initialPercent=70,mode=Mode.CHARGING))))
  assertNull(SessionArithmetic.group(listOf(a,a.copy(startedAtMs=3_900_001L,endedAtMs=4_900_001L,initialPercent=70))))
  assertNull(SessionArithmetic.group(listOf(a.copy(observedDurationMs=-1))))
  assertNull(SessionArithmetic.group(listOf(a.copy(observedDurationMs=7_200_000L))))
 }

 @Test fun counterDirectionReversalDoesNotInventRate() {
  val a=part(0,3_600_000L,3_600_000L,80,70)
  val b=part(3_600_000L,7_200_000L,3_600_000L,70,75)
  assertNull(SessionArithmetic.group(listOf(a,b))!!.ratePercentHour)
 }

 @Test fun theNewSessionStartsWithoutInheritingTheOldStateTime() {
  assertTrue(BatteryMath.startsNewSession(Mode.CHARGING,Mode.DISCHARGING,
   10_000L,70_000L,100_000L,160_000L))
  assertEquals(0L,BatteryMath.observedDuration(0L,0L,70_000L,false,0))
  assertEquals(60_000L,BatteryMath.observedDuration(0L,70_000L,130_000L,false,1))
 }
}
