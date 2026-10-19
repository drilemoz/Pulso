package br.com.pulsobateria.core

/** Contexto observável de uma previsão; não representa probabilidade de acerto. */
enum class ForecastEvidence(val label: String) {
 LIVE_ONLY("Somente tendência atual"),
 SINGLE_SESSION("Histórico limitado: uma sessão"),
 MULTIPLE_SESSIONS("Histórico de múltiplas sessões"),
 MIXED_LIMITED("Tendência atual + histórico limitado"),
 MIXED_MULTIPLE("Tendência atual + múltiplas sessões"),
 UNKNOWN("Origem não classificada")
}

object ForecastEvidenceClassifier {
 fun charge(eta: ChargeEta?, history: HistoricalCharging?): ForecastEvidence? {
  if (eta == null) return null
  val live = eta.basis.startsWith("Carga atual")
  val historical = eta.basis.contains("histórico", ignoreCase=true) ||
   eta.basis.startsWith("Recargas") || eta.basis.startsWith("Histórico")
  val sessions = history?.sessionsUsed ?: 0
  return classify(live, historical, sessions)
 }
 fun discharge(eta: DischargeEta?, history: HistoricalAutonomy?): ForecastEvidence? {
  if (eta == null) return null
  val live = eta.basis.startsWith("Consumo atual")
  val historical = eta.basis.contains("sessões", ignoreCase=true) ||
   eta.basis.contains("histórica", ignoreCase=true)
  return classify(live, historical, history?.sessionsUsed ?: 0)
 }
 private fun classify(live: Boolean, historical: Boolean, sessions: Int): ForecastEvidence = when {
  live && historical && sessions >= 2 -> ForecastEvidence.MIXED_MULTIPLE
  live && historical -> ForecastEvidence.MIXED_LIMITED
  live -> ForecastEvidence.LIVE_ONLY
  historical && sessions >= 2 -> ForecastEvidence.MULTIPLE_SESSIONS
  historical -> ForecastEvidence.SINGLE_SESSION
  else -> ForecastEvidence.UNKNOWN
 }
}
