package org.ditw.book.cmdS

import scala.collection.mutable.ListBuffer

object CmdPtn {
  trait TCmd {
    def execute():Unit
    def undo():Unit
  }

  class CashRegister(var sum:Int) {
    def add(amount:Int):Unit = sum += amount
    def deduct(amount:Int):Unit = sum -= amount

    def trace(msg:String):Unit = println(s"$msg: $sum")
  }

  class Purchase(private val cashReg:CashRegister, private val amount:Int) extends TCmd {
    override def execute(): Unit = {
      cashReg.add(amount)
    }

    override def undo(): Unit = {
      cashReg.deduct(amount)
    }
  }

  def makePurchaseByClosure(cashReg:CashRegister, amount:Int):TCmd = new TCmd {
    override def execute(): Unit = {
      cashReg.add(amount)
    }
    override def undo(): Unit = cashReg.deduct(amount)
  }

  class Refund(private val cashReg:CashRegister, private val amount:Int) extends TCmd {
    override def execute(): Unit = {
      cashReg.deduct(amount)
    }

    override def undo(): Unit = {
      cashReg.add(amount)
    }
  }
  def makeRefundByClosure(cashReg:CashRegister, amount:Int):TCmd = new TCmd {
    override def execute(): Unit = cashReg.deduct(amount)
    override def undo(): Unit = cashReg.add(amount)
  }

  class CmdInvoker {
    private val cmds = ListBuffer[TCmd]()

    def execute(cmd: TCmd):Unit = {
      cmd.execute()
      cmds.insert(0, cmd)
    }

    def delayedExecute(cmd: TCmd):Unit = {
      cmds.insert(0, cmd)
    }

    def executeAll():Unit = {
      cmds.reverse.foreach(_.execute())
    }

    def undo1():Unit = {
      val hc = cmds.head
      hc.undo()
      cmds.remove(0)
    }

    def undoAll():Unit = {
      cmds.foreach(_.undo())
      cmds.clear()
    }

    def trace():Unit = println(s"Cmds: ${cmds.size}")
  }
}
