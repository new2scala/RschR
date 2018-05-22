package org.ditw.book.tinyWebS

object ClosureTest extends App {

  def run1():Unit = {
    var tmpv = "initial"
    def f1(v2:Int):Unit = println(s"$tmpv - $v2")

    f1(1)
    tmpv = "changed 1"
    f1(2)
  }

  //run1

  class Foo(f:String => Unit) {
    def apply(n:String):Unit = f(n)
  }

  def run2():Unit = {
    var tmpv = "initial"
    def f1(v2:String):Unit = println(s"$v2 $tmpv")

    val foo1 = new Foo(f1)
    foo1("t1")

    tmpv = "changed 1"
    foo1("t2")

  }

  run2
}
