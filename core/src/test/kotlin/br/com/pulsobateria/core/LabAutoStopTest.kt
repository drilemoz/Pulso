package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class LabAutoStopTest {
 @Test fun exigeCargaAnteriorEConsecutiva() {
  assertFalse(LabAutoStop.shouldStop("AUTO",false,false,100_000,280_000,250_000))
  assertFalse(LabAutoStop.shouldStop("AUTO",true,true,100_000,280_000,250_000))
  assertFalse(LabAutoStop.shouldStop("SOLAR",true,false,100_000,280_000,250_000))
  assertFalse(LabAutoStop.shouldStop("AUTO",true,false,null,280_000,250_000))
  assertFalse(LabAutoStop.shouldStop("AUTO",true,false,100_000,220_000,190_000))
  assertTrue(LabAutoStop.shouldStop("AUTO",true,false,100_000,280_000,250_000))
 }
 @Test fun rejeitaLacunasEFalsosTempos() {
  assertFalse(LabAutoStop.shouldStop("AUTO",true,false,100_000,280_000,180_000))
  assertFalse(LabAutoStop.shouldStop("AUTO",true,false,100_000,280_000,280_000))
  assertFalse(LabAutoStop.shouldStop("AUTO",true,false,100_000,280_000,null))
 }
}
