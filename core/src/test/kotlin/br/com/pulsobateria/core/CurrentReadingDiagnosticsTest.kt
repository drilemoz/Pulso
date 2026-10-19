package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class CurrentReadingDiagnosticsTest {
 @Test fun sourcesAreClassifiedWithoutChangingMeasurements() {
  val sources = mapOf(CurrentSource.NOW to CurrentReadingKind.MEASURED,
   CurrentSource.AVERAGE to CurrentReadingKind.AVERAGED,
   CurrentSource.COUNTER to CurrentReadingKind.AVERAGED,
   CurrentSource.CAPACITY to CurrentReadingKind.ESTIMATED,
   CurrentSource.NONE to CurrentReadingKind.UNAVAILABLE)
  sources.forEach { (source, expected) ->
   assertEquals(expected,CurrentReadingDiagnostics.classify(CurrentResult(source=source),1000,1000).kind)
  }
 }
 @Test fun staleBoundaryAndClockRollback() {
  val reading=CurrentResult(source=CurrentSource.NOW)
  assertFalse(CurrentReadingDiagnostics.classify(reading,1000,61000).stale)
  assertTrue(CurrentReadingDiagnostics.classify(reading,1000,61001).stale)
  assertTrue(CurrentReadingDiagnostics.classify(reading,0,61001).stale)
  assertTrue(CurrentReadingDiagnostics.classify(reading,61002,61001).stale)
 }
 @Test fun estimateAndUnavailableAreNotPresentedAsSensorMeasurements() {
  assertTrue(CurrentReadingDiagnostics.classify(CurrentResult(source=CurrentSource.CAPACITY),1000,1000).description.contains("não medição"))
  assertTrue(CurrentReadingDiagnostics.classify(CurrentResult(),1000,1000).description.contains("Nenhuma"))
 }
}
