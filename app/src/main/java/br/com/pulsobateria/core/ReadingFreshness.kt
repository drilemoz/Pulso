package br.com.pulsobateria.core

/** Idade da consulta ao Android; não representa a idade interna do sensor. */
object ReadingFreshness {
 fun describe(updatedAtMs: Long, nowMs: Long): String {
  if (updatedAtMs <= 0L || nowMs < updatedAtMs) return "Sem horário válido"
  val ageSeconds = (nowMs - updatedAtMs) / 1000L
  return when {
   ageSeconds < 5L -> "Consulta recente (<5 s)"
   ageSeconds < 60L -> "Há ${ageSeconds} s"
   ageSeconds < 3600L -> "Há ${ageSeconds / 60L} min"
   else -> "Há ${ageSeconds / 3600L} h"
  }
 }
}
