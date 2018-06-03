package org.ditw.rschr

object OrderedTests extends App {

  case class Val(i:Int) extends Ordered[Val] {
    override def compare(that: Val): Int = i - that.i
  }

  val valList = List(Val(1), Val(2), Val(1), Val(3))
  println(valList.sorted)


}
