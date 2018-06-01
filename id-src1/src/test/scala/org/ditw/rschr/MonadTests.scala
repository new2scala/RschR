package org.ditw.rschr

object MonadTests extends App {

  val f = (i:Int) => {
    List(s"S${i-1}", s"S$i", s"S${i+1}")
  }
  val g = (s:String) => {
    "ABC".map(c => s"$s$c").toList
  }

  val l1 = List(2)
  val l11 = l1.flatMap(f)
  println(l11)
  val l12 = f(2)
  println(l12)

  val l21 = l1.flatMap(x => List(x))
  println(l21)

  val l111 = l1.flatMap(f).flatMap(g)
  val l112 = l1.flatMap(x => f(x).flatMap(g))
  println(l111)
  println(l112)
}
