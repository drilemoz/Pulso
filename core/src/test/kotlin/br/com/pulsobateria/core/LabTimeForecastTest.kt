package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class LabTimeForecastTest {
 private val start = 1_700_000_000_000L
 private val read = start + 30*60_000L
 private val now = read + 15_000L
 private fun eta(kind: String = "SOLAR", running: Boolean = true, started: Long = start,
  readAt: Long? = read, checkedAt: Long = now, initial: Int? = 30, current: Int? = 40,
  charging: Long = 29*60_000L, mode: String? = "CHARGING") =
  LabTimeForecast.fullAt(kind, running, started, readAt, checkedAt, initial, current, charging, mode)

 @Test fun observaTaxaPorPontosPercentuaisSemUsarCorrenteOuCapacidade() {
  // +10 p.p. em 30 min -> 20 p.p./h, 60 p.p. faltantes -> 3 h.
  assertEquals(read + 3*3_600_000L, eta())
  assertEquals(read + 3*3_600_000L, eta(kind="ELECTRIC"))
  assertEquals(read + 3*3_600_000L, eta(kind="AUTO"))
 }
 @Test fun evitaEstimativaEmConsumoOuTesteConcluido() {
  assertNull(eta(kind="DISCHARGE"))
  assertNull(eta(running=false))
  assertNull(eta(mode="DISCHARGING"))
  assertNull(eta(mode="FULL"))
 }
 @Test fun exigeProgressoRealObservado() {
  assertNull(eta(initial=40,current=40))
  assertNull(eta(initial=40,current=41))
  assertNull(eta(initial=40,current=39))
  assertNull(eta(initial=null))
  assertNull(eta(current=null))
  assertNull(eta(current=100))
  assertNull(eta(initial=101))
 }
 @Test fun naoProjetaComLeituraAntigaOuNoFuturo() {
  assertNull(eta(checkedAt=read+90_001L))
  assertNull(eta(checkedAt=read-1L))
  assertNull(eta(readAt=null))
  assertNull(eta(readAt=start-1))
 }
 @Test fun exigeCincoMinutosETempoRealDeCarga() {
  assertNull(eta(readAt=start+4*60_000L,checkedAt=start+4*60_000L,charging=4*60_000L))
  assertNull(eta(charging=2*60_000L))
  assertNull(eta(charging=20*60_000L)) // Cobertura de carga abaixo de 75%.
  assertNull(eta(charging=31*60_000L)) // Mais carga observada que duração física.
 }
 @Test fun descartaEtasAcimaDeSeteDiasEInstantesInvalidos() {
  assertNull(eta(started=-1L))
  assertNull(eta(initial=1,current=3,charging=23L*60L*60_000L,
   readAt=start+24L*60L*60_000L,checkedAt=start+24L*60L*60_000L)) // >7 dias.
  assertNull(eta(started=Long.MAX_VALUE-1000L))
 }
}
