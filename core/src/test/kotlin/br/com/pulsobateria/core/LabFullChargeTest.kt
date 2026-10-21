package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class LabFullChargeTest {
 @Test fun encerraCargaSolarSomenteComCemPorCentoNovo() {
  assertTrue(LabFullCharge.shouldStop("SOLAR",true,100,21_000,10_000,20_000))
  assertFalse(LabFullCharge.shouldStop("SOLAR",true,99,21_000,10_000,20_000))
  assertFalse(LabFullCharge.shouldStop("SOLAR",true,null,21_000,10_000,20_000))
  assertFalse(LabFullCharge.shouldStop("SOLAR",true,100,20_000,10_000,20_000))
  assertFalse(LabFullCharge.shouldStop("SOLAR",true,100,10_000,10_000,null))
 }

 @Test fun encerraEletricoMasNuncaDescarga() {
  assertTrue(LabFullCharge.shouldStop("ELECTRIC",true,100,21_000,10_000,null))
  assertTrue(LabFullCharge.shouldStop("AUTO",true,100,21_000,10_000,null))
  assertFalse(LabFullCharge.shouldStop("AUTO",true,99,21_000,10_000,null))
  assertFalse(LabFullCharge.shouldStop("DISCHARGE",true,100,21_000,10_000,null))
  assertFalse(LabFullCharge.shouldStop("SOLAR",false,100,21_000,10_000,null))
  assertFalse(LabFullCharge.shouldStop("ELECTRIC",false,100,21_000,10_000,null))
  assertFalse(LabFullCharge.shouldStop("UNKNOWN",true,100,21_000,10_000,null))
 }
}
