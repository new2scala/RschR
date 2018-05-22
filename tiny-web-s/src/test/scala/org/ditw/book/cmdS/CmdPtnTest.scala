package org.ditw.book.cmdS

import org.ditw.book.cmdS.CmdPtn._

object CmdPtnTest extends App {

  def testClassic():Unit = {
    val ci = new CmdInvoker
    val cr = new CashRegister(0)

    ci.execute(new Purchase(cr, 10))
    ci.execute(new Purchase(cr, 20))
    ci.execute(new Refund(cr, 12))
    ci.execute(new Purchase(cr, 15))

    ci.trace()
    cr.trace("after 4 cmds")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")
  }

  def testClosure():Unit = {
    val ci = new CmdInvoker
    val cr = new CashRegister(0)

    ci.execute(makePurchaseByClosure(cr, 10))
    ci.execute(makePurchaseByClosure(cr, 20))
    ci.execute(makeRefundByClosure(cr, 12))
    ci.execute(makePurchaseByClosure(cr, 15))

    ci.trace()
    cr.trace("after 4 cmds")


  }

  def testClosureRev():Unit = {
    val ci = new CmdInvoker
    val cr = new CashRegister(0)

    ci.delayedExecute(makePurchaseByClosure(cr, 10))
    ci.delayedExecute(makePurchaseByClosure(cr, 20))
    ci.delayedExecute(makeRefundByClosure(cr, 12))
    ci.delayedExecute(makePurchaseByClosure(cr, 15))

    ci.executeAll()

    ci.trace()
    cr.trace("after 4 cmds")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")

  }

  def testClosure1():Unit = {
    val ci = new CmdInvoker
    var cr = new CashRegister(0)
    val cr0 = cr

    ci.delayedExecute(makePurchaseByClosure(cr, 10))

    cr = new CashRegister(11)
    ci.delayedExecute(makePurchaseByClosure(cr, 20))
    ci.delayedExecute(makeRefundByClosure(cr, 12))
    ci.delayedExecute(makePurchaseByClosure(cr, 15))

    ci.executeAll()

    ci.trace()
    cr.trace("after 4 cmds")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")
    ci.undo1()
    ci.trace()
    cr.trace("after undo 1")

  }

  testClosure1()
}
