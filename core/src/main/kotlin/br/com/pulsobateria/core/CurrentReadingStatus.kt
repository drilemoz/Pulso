package br.com.pulsobateria.core

/** Diagnóstico de apresentação. A idade é da consulta, não da amostra interna do sensor. */
enum class CurrentReadingKind(val label: String) {
 MEASURED("Medição instantânea do Android"),
 AVERAGED("Média informada pelo Android ou calculada do contador"),
 ESTIMATED("Estimativa baseada no histórico e capacidade informada"),
 UNAVAILABLE("Corrente indisponível")
}

data class CurrentReadingStatus(val kind: CurrentReadingKind, val stale: Boolean, val description: String)

object CurrentReadingDiagnostics {
 const val STALE_AFTER_MS = 60_000L
 fun classify(result: CurrentResult, updatedAtMs: Long, nowMs: Long): CurrentReadingStatus {
  val kind = when(result.source) {
   CurrentSource.NOW -> CurrentReadingKind.MEASURED
   CurrentSource.AVERAGE, CurrentSource.COUNTER -> CurrentReadingKind.AVERAGED
   CurrentSource.CAPACITY -> CurrentReadingKind.ESTIMATED
   CurrentSource.NONE -> CurrentReadingKind.UNAVAILABLE
  }
  val stale = updatedAtMs <= 0L || nowMs < updatedAtMs || nowMs-updatedAtMs > STALE_AFTER_MS
  val description = when {
   kind == CurrentReadingKind.UNAVAILABLE -> "Nenhuma corrente utilizável nesta consulta."
   stale -> "Consulta antiga ou sem horário válido; o valor pode estar desatualizado."
   kind == CurrentReadingKind.ESTIMATED -> "Estimativa, não medição do sensor."
   else -> "Consulta recente ao Android; atualização interna do sensor não verificável."
  }
  return CurrentReadingStatus(kind,stale,description)
 }
}
