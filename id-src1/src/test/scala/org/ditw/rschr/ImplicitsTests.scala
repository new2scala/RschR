package org.ditw.rschr

object ImplicitsTests extends App {

  class Container[A](v:A) {
    def size(implicit evd: A <:< Seq[_]):Int = v.size
    def sizeInt(implicit evd: A =:= Seq[Int]):Int = v.size

    override def toString: String = s"Contains: $v"
  }

  val containerStr = new Container("abc")
  println(containerStr)

  //do not compile: println(containerStr.size)

  val containerSeq = new Container(Seq(1, 2, 3))
  println(containerSeq.size)
  println(containerSeq.sizeInt)

  val containerIndexSeq = new Container(IndexedSeq(2, "4"))
  println(containerIndexSeq.size)
  //do not compile: println(containerIndexSeq.sizeInt)
}
