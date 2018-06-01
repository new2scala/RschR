package org.ditw.rschr

object TypeClassTests extends App {

  // Basically what type classes allow is to add functionality to existing classes without needing to touch the existing classes.
  // step 1. create a trait
  // step 2. define implicit val/objects implementing the trait for different classes (the classes whose functionalities needs to be extended
  // step 3. define _Ops class to implicitly converting a class A instance to an _Ops[A] instance, which will in turn use the type classes defined
  //   to perform operation


  trait Dupl[A, B] {
    def dup(a:A):B
  }

  object Duplicates {
    implicit val DuplInt2Int = new Dupl[Int, Int] {
      override def dup(a: Int): Int = a+a
    }

    implicit val DuplInt2Str = new Dupl[String, Int] {
      override def dup(a: String): Int = a.toInt*2
    }
  }

  import Duplicates._

  def output[A, B](v:A)(implicit d:Dupl[A, B]):B = {
    d.dup(v)
  }

  implicit class DuplOps[A, B](val a:A)(implicit d:Dupl[A,B]) {
    def x2:B = d.dup(a)
  }

  val dup2:Int = "2".x2
  val dup22:Int = 2.x2

  // undefined: 2L.x2

  println(dup2)
}
