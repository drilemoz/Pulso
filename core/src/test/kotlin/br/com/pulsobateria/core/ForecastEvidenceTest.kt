package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class ForecastEvidenceTest {
 @Test fun absentPredictionHasNoEvidence() {
  assertNull(ForecastEvidenceClassifier.charge(null,null))
  assertNull(ForecastEvidenceClassifier.discharge(null,null))
 }
 @Test fun chargeEvidenceTracksActualSessionCount() {
  val eta=ChargeEta(1000,2000,"Carga atual + histórico")
  assertEquals(ForecastEvidence.MIXED_LIMITED,ForecastEvidenceClassifier.charge(eta,null))
  val h=HistoricalCharging(1000,10.0,3,null,0,null,0,null,false)
  assertEquals(ForecastEvidence.MIXED_MULTIPLE,ForecastEvidenceClassifier.charge(eta,h))
  assertEquals(ForecastEvidence.LIVE_ONLY,ForecastEvidenceClassifier.charge(eta.copy(basis="Carga atual"),h))
 }
 @Test fun dischargeEvidenceDoesNotClaimPrecision() {
  val eta=DischargeEta(1000,2000,"Média histórica de descarga")
  val h=HistoricalAutonomy(1000,10.0,1,null,0)
  assertEquals(ForecastEvidence.SINGLE_SESSION,ForecastEvidenceClassifier.discharge(eta,h))
  assertEquals(ForecastEvidence.MULTIPLE_SESSIONS,ForecastEvidenceClassifier.discharge(eta,h.copy(sessionsUsed=2)))
  assertEquals(ForecastEvidence.UNKNOWN,ForecastEvidenceClassifier.discharge(eta.copy(basis="Previsão externa"),h))
 }
}
