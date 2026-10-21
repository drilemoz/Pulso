package br.com.pulsobateria.core

import org.junit.Assert.*
import org.junit.Test

class RemoteChargeCompletionTest {
 @Test fun notificaUmaUnicaVezAos100DepoisDeAcompanharOTeste() {
  val tracker=RemoteChargeCompletion()
  assertNull(tracker.observe("celularA",23,"AUTO",false,"FULL"))
  assertNull(tracker.observe("celularA",23,"AUTO",true,"RUNNING"))
  assertNull(tracker.observe("celularA",23,"AUTO",true,"RUNNING"))
  assertTrue(tracker.observe("celularA",23,"AUTO",false,"FULL")!!.contains("100%"))
  assertNull(tracker.observe("celularA",23,"AUTO",false,"FULL"))
  assertNull(tracker.observe("celularA",23,"AUTO",true,"RUNNING"))
  assertNull(tracker.observe("celularA",23,"AUTO",false,"FULL"))
 }
 @Test fun somenteCargaComConclusaoValida() {
  val tracker=RemoteChargeCompletion()
  assertNull(tracker.observe("a",1,"DISCHARGE",true,"RUNNING"))
  assertNull(tracker.observe("a",1,"DISCHARGE",false,"STOPPED"))
  assertNull(tracker.observe("a",2,"ELECTRIC",true,"RUNNING"))
  assertNull(tracker.observe("a",2,"ELECTRIC",false,"RUNNING"))
  assertTrue(tracker.observe("a",2,"ELECTRIC",false,"UNPLUGGED")!!.contains("desconectada"))
  assertNull(tracker.observe("a",2,"ELECTRIC",false,"UNPLUGGED"))
  assertNull(tracker.observe("b",2,"ELECTRIC",false,"UNPLUGGED"))
 }
 @Test fun novaSessaoGeraOutraNotificacao() {
  val tracker=RemoteChargeCompletion()
  assertNull(tracker.observe("a",9,"SOLAR",true,"RUNNING"))
  assertNotNull(tracker.observe("a",9,"SOLAR",false,"STOPPED"))
  assertNull(tracker.observe("a",10,"SOLAR",true,"RUNNING"))
  assertNotNull(tracker.observe("a",10,"SOLAR",false,"FULL"))
 }
}
