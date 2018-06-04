package org.ditw.rschr

import scala.reflect.ClassTag

/**
  * Created by dev on 2018-06-04.
  */
object GeneralizedTypeConstraint extends App {

  def getPairs[S](s1:S, s2:S)(implicit ev:ClassTag[S]):(S, S) = {
    println(ev.toString())
    s1 -> s2
  }

  println(getPairs("1", 12))
  println(getPairs("1", new Integer(12)))

  def getPairs2[S, T](s1:S, s2:T)(implicit ev:T =:= S):(S, S) = {
    //println(ev.)
    s1 -> s2
  }
  println(getPairs2("1", 12.toString))
  //do not compile: println(getPairs2("1", 12))

  def getPair3[S <: T, T](s1:S, s2:T)(implicit ev1:ClassTag[S], ev2:ClassTag[T]):(S, T) = {
    println(s"S:$ev1, T:$ev2")
    s1 -> s2
  }

  println(getPair3("1", 12))

  def getPair4[S, T](s1:S, s2:T)(implicit ev:S <:< T):(S, T) = {
    s1 -> s2
  }
  def getPair5[S, T](s1:S, s2:T)(implicit ev:S =:= T):(S, T) = {
    s1 -> s2
  }

  trait Animal {
    val name:String
  }
  case class Dog(name:String) extends Animal

  println(getPair4(1, 1))
  val animal1 = new Animal {
    override val name: String = "ani1"
  }
  //println(getPair4(animal1, Dog("d1")))
  println(getPair4(Dog("d1"), animal1))
  val dog1:Animal = Dog("dog1")
  println(getPair5(dog1, animal1))
  println(getPair4(dog1, animal1))
}
