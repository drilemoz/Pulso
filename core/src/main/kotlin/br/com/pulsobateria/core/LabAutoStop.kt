package br.com.pulsobateria.core

/** Fecha o teste AUTO somente após 3 minutos de observações contínuas sem fonte,
 * depois de já ter confirmado carregamento. Não preenche lacunas nem presume que
 * um cabo desconectado significa bateria cheia.
 */
object LabAutoStop {
 const val MIN_DISCONNECTED_MS = 180_000L
 const val MAX_SAMPLE_GAP_MS = 90_000L
 fun shouldStop(kind: String, previouslyCharged: Boolean, plugged: Boolean?,
  disconnectedSinceMs: Long?, sampleTimeMs: Long, previousSampleMs: Long?): Boolean =
  kind == "AUTO" && previouslyCharged && plugged == false &&
  disconnectedSinceMs != null && previousSampleMs != null &&
  sampleTimeMs > previousSampleMs && sampleTimeMs >= disconnectedSinceMs &&
  sampleTimeMs - previousSampleMs <= MAX_SAMPLE_GAP_MS &&
  sampleTimeMs - disconnectedSinceMs >= MIN_DISCONNECTED_MS
}
