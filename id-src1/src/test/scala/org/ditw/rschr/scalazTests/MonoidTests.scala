package org.ditw.rschr.scalazTests

object MonoidTests extends App {

  import scalaz._
  import Scalaz._

  def sumT[A](xs:List[A])(implicit m:Monoid[A]):A = xs.foldLeft(m.zero)((x,y) => m.append(x,y))

  val s:Int = sumT(List(1, 2))
  if (1 ≠ s) println("no")


  trait Eq2[A] {
    def isEqual(a1:A, a2:A):Boolean
  }

  implicit object intEq2 extends Eq2[Int] {
    override def isEqual(a1: Int, a2: Int): Boolean = a1 == a2
  }

  implicit class Eq2Ops[A](val v:A)(implicit e:Eq2[A]) {
    def ====(o:A):Boolean = e.isEqual(v, o)
    def ==/==(o:A):Boolean = !e.isEqual(v, o)
  }

  //def isEqual(a1:Int, a2:Int)(implicit m:Eq2[Int]):Boolean = m.isEqual(a1, a2)

  if (s ==/== 1) println("eq1")
//  class Eq2Ops[A](val a:A) {
//
//  }

}
