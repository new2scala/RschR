package org.ditw.rschr

import scala.collection.mutable.ListBuffer

object StackableTraitsTest extends App {

  trait IntQ {
    def get:Option[Int]
    def put(x:Int):Unit
  }

  class BasicQ extends IntQ {
    private val buf = ListBuffer[Int]()

    def get:Option[Int] = if (buf.nonEmpty) Option(buf.remove(0)) else None
    def put(x:Int):Unit = buf += x
  }

  def putGetTest(msg:String, q:IntQ):Unit = {
    println(s"============$msg starting ...")
    q.put(-2)
    q.put(-1)
    q.put(0)
    q.put(1)
    println(q.get)
    println(q.get)
    println(q.get)
    println(q.get)
    println(s"============$msg done")
  }

  val bqTest = new BasicQ
  putGetTest("BasicQ tests", bqTest)

  trait Doubling extends IntQ {
    abstract override def put(x:Int):Unit = {
      super.put(2*x)
    }
  }

  class DoublingQ extends BasicQ with Doubling

  val dq = new DoublingQ
  putGetTest("DoublingQ tests", dq)

  trait IncQ extends IntQ {
    abstract override def put(x:Int): Unit = {
      super.put(x+1)
    }
  }

  trait FilteringQ extends IntQ {
    abstract override def put(x:Int):Unit = {
      if (x >= 0) super.put(x)
    }
  }

  class IncFilteringQ extends BasicQ with FilteringQ with IncQ

  val ifq = new IncFilteringQ
  putGetTest("IncFilteringQ tests", ifq)

  class FilteringIncQ extends BasicQ with IncQ with FilteringQ
  val fiq = new FilteringIncQ
  putGetTest("FilteringIncQ tests", fiq)

  class DoublingFilteringIncQ extends BasicQ with IncQ with FilteringQ with Doubling
  val dfiq = new DoublingFilteringIncQ
  putGetTest("DoublingFilteringIncQ tests", dfiq)

  class IncDoublingFilteringQ extends BasicQ with FilteringQ with Doubling with IncQ
  val idfq = new IncDoublingFilteringQ
  putGetTest("IncDoublingFilteringQ tests", idfq)

}
