package org.ditw.rschr.patterns

/**
  * Created by dev on 2018-06-01.
  */
object VarianceTests extends App {

  class Lst[T] { self:Lst[T] =>
    private var _l = List[T]()
    def add(t:T):Lst[T] = {
      _l = _l ::: List(t)
      self
    }
    def toList:List[T] = _l
  }

  def lst[T](t:T):Lst[T] = {
    val r = new Lst[T]()
    r.add(t)
  }

  abstract class Animal {
    def name:String
  }

  case class Cat(name:String) extends Animal
  case class Dog(name:String) extends Animal

  def traceLst(lst:Lst[Animal]):Unit = {
    lst.toList.foreach(i => println(i.name))
  }

  val catLst:Lst[Cat] = lst(Cat("c1"))
  val catAniLst:Lst[Animal] = lst(Cat("ca1"))
  traceLst(catAniLst)
  // do not compile: traceLst(catLst)

  class Lst1[+T](items:List[T]) { self:Lst1[T] =>
    private val _l = items
    def add[U >: T](t:U):Lst1[U] = {
      val newItems = _l ::: List(t)
      new Lst1(newItems)
    }
    def toList:List[T] = _l
  }
  def traceLst1(lst:Lst1[Animal]):Unit = {
    println("---------------")
    lst.toList.foreach(i => println(i.name))
  }

  val catLst1:Lst1[Cat] = new Lst1(List(Cat("c11")))
  traceLst1(catLst1)
  val dogLst1:Lst1[Dog] = new Lst1(List(Dog("d11")))
  traceLst1(dogLst1)

  val catDogLst:Lst1[Animal] = catLst1.add(dogLst1.toList.head)

  traceLst1(catDogLst)

  def traceMap(m:Map[String, Animal]):Unit = {
    println("--------------------")
    m.toIndexedSeq.sortBy(_._1).foreach(println)
  }

  val m1 = List(Cat("cc1"), Cat("cc2")).map(a => a.name -> a).toMap
  traceMap(m1)

  trait Feed[-T] {
    val food:String = "meat"
    def feed(t:T):String = {
      s"Feeding [${t.toString}] with [$food]"
    }
  }

  def feed[T](t:T, f:Feed[T]):Unit = {
    println("--------- feed")
    println(f.feed(t))
  }
  val cat:Cat = Cat("ccc1")
  val feedAnimal = new Feed[Animal] { }
  val feedCat = new Feed[Cat] {
    override val food: String = "cat food"
  }
  feed(cat, feedAnimal)
  feed(cat, feedCat)
  val dog:Dog = Dog("ddd1")
  val feedDog = new Feed[Dog] {
    override val food: String = "dog food"
  }
  feed(dog, feedAnimal)
  feed(dog, feedDog)
}
