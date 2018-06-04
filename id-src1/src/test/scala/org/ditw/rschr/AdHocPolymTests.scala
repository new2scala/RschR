package org.ditw.rschr

/**
  * Created by dev on 2018-06-04.
  */
object AdHocPolymTests extends App {

  object TestWithImplicit {
    trait Appendable[T] {
      def append(a:T):T
    }

    class AppInt(i:Int) extends Appendable[Int] {
      override def append(a: Int): Int = i + a
    }

    class AppStr(s:String) extends Appendable[String] {
      override def append(a: String): String = s.concat(a)
    }

    def appendItem[A](a:A, b:A)(implicit ev: A => Appendable[A]) = a.append(b)
    implicit def int2Appendable(i:Int):AppInt = new AppInt(i)
    def int2Appendable2(i:Int):AppInt = {
      val t2 = new AppInt(i+1)
      t2
    }

    def run:Unit = {
      println("Testing with implicit conversion")
      val intApp = appendItem(1, 5)
      println(intApp)
      println(int2Appendable2(1).append(3))
    }

  }

  TestWithImplicit.run

  object TestWithTypeClass {

    trait Appendable2[T] {
      def append(a:T, b:T):T
    }

    def appendItem[A](a:A, b:A)(implicit ev:Appendable2[A]):A = ev.append(a, b)

    object Appendable2 {
      implicit val AppInt:Appendable2[Int] = new Appendable2[Int] {
        override def append(a: Int, b: Int): Int = a+b
      }
    }

    implicit val AppInt2:Appendable2[Int] = new Appendable2[Int] {
      override def append(a: Int, b: Int): Int = (a.toString + b.toString).toInt
    }


    def run:Unit = {
      println(appendItem(3, 4))
    }
  }


  TestWithTypeClass.run

  object OrderedOrderingTest {
    val seq = Seq(1, 2, 3, 4, 15)

    implicit val newOrdering:Ordering[Int] = new Ordering[Int] {
      override def compare(x: Int, y: Int): Int = {
        if (x == y) 0
        else {
          val x1 = if (x == 3) x+10 else x
          val y1 = if (y == 3) y+10 else y
          x1-y1
        }
      }
    }

    def run:Unit = {
      println(seq.sorted)
      println(seq.sorted(newOrdering.reverse))
    }

  }

  OrderedOrderingTest.run
}
