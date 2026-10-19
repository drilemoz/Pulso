package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class LabComparisonQualityTest {
 private fun sample(total: Long=120_000L, observed: Long=120_000L, initial: Int?=50,
                    final: Int?=55, mean: Double?=250.0) =
  LabComparisonQuality.TestSummary(total,observed,0L,initial,final,mean)
 @Test fun completeComparableTestsHaveNoWarnings() {
  assertTrue(LabComparisonQuality.warnings(sample(),sample()).isEmpty())
 }
 @Test fun missingMeasurementsRemainExplicit() {
  val warnings=LabComparisonQuality.warnings(sample(observed=50_000L,initial=null,mean=null),sample())
  assertTrue(warnings.any { it.contains("80%") })
  assertTrue(warnings.any { it.contains("bateria") })
  assertTrue(warnings.any { it.contains("Corrente") })
 }
 @Test fun durationsAndInvalidValuesAreFlagged() {
  val warnings=LabComparisonQuality.warnings(sample(total=0L),sample(total=200_000L,observed=200_000L))
  assertTrue(warnings.any { it.contains("duração válida") })
  assertTrue(LabComparisonQuality.warnings(sample(),sample(total=200_000L,observed=200_000L)).any { it.contains("25%") })
  assertTrue(LabComparisonQuality.warnings(sample(initial=110),sample()).any { it.contains("bateria") })
 }
}
