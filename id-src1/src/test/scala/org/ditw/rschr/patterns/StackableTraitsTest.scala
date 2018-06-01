package org.ditw.rschr.patterns

object StackableTraitsTest extends App {

  abstract class GrandFather {
    def f1:Boolean
    def f2:Boolean = f1
    def f3:String = "GrandFather"
  }

  trait Father extends GrandFather {
    override def f1: Boolean = true

    override def f3: String = s"Father -${super.f3}"
  }

  trait Son1 extends Father {
    override def f1: Boolean = true
    override def f3: String = s"Son1 -${super.f3}"
  }

  trait Son2 extends Father {
    override def f1: Boolean = true
    override def f3: String = s"Son2 -${super.f3}"
  }

  trait Daughter1 extends Father {
    override def f1: Boolean = false
    override def f3: String = s"Daughter1 -${super.f3}"
  }

  trait GrandSon extends Son1

  class Tc1 extends GrandFather with Son1 with Daughter1
  class Tc2 extends GrandFather with Daughter1 with Son1

  println(new Tc1().f2) // false
  println(new Tc2().f2) // true

  class Tc3 extends Daughter1 with GrandSon
  class Tc4 extends GrandSon with Daughter1
  println(new Tc3().f3) // Son1 -Daughter1 -Father -GrandFather
  println(new Tc4().f3) // Daughter1 -Son1 -Father -GrandFather

}
