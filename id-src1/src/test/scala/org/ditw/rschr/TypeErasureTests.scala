package org.ditw.rschr

import scala.reflect.ClassTag

object TypeErasureTests extends App {

  def ext[T](l:List[Any])(implicit evd:ClassTag[T]):List[T] = l.flatMap {
    //  same as: def ext[T:ClassTag](l:List[Any]):List[T] = l.flatMap {
    case e:T => Some(e)
    case _ => None
  }

  def ext2[T](l:List[Any])(implicit evd:ClassTag[T]):List[T] = l.flatMap {
    case elem @ evd(_:T) => evd.unapply(elem) //Some()
    case _ => None
  }
  import scala.reflect.runtime.universe._
  def ext3[T](l:List[Any])(implicit evd:TypeTag[T]):List[T] = l.flatMap {
    //  same as: def ext[T:ClassTag](l:List[Any]):List[T] = l.flatMap {
    case e:T => Some(e)
    case _ => None
  }


  val l1 = List(1, "abc", 2, false)

  val res = ext[String](l1)
  val res2 = ext2[String](l1)
  println(res)
  println(res2)

  val ll = List(1, "a", List(1, 2), List("a", "b"))
  val ll2 = List(List(1, 2), List("a", "b"))

  val res3 = ext[List[Int]](ll) // higher level
  val res4 = ext2[List[Int]](ll) // higher level
  val res5 = ext3[List[Int]](ll2) // higher level

  println(res3)
  println(res4) // prints: List(List(1, 2), List(a, b)) because ClassTags cannot differentiate on a higher level (of generic types)
  println(res5) // prints: List(List(1, 2), List(a, b)) because ClassTags cannot differentiate on a higher level (of generic types)

  def rec[T](x:T)(implicit tt:TypeTag[T]):String = {
    tt.tpe match {
      case TypeRef(uType, usymbol, args) =>
        List(s"Type: $uType", s"Symbol: $usymbol", s"Args: ${args.mkString(",")}").mkString("\n\t")
    }
  }

  val ll21:List[Int] = List(1, 2)
  val ll22:List[Any] = List(1, 2)
  val ll23:Any = List(1, 2)

  println(rec(ll21))
  println(rec(ll22))
  println(rec(ll))
  println(rec(ll2))
  println(rec(ll23))
  println(rec(List(IndexedSeq(1, 2))))

  import scala.reflect.classTag
  val ct = classTag[String]
  val tt = typeTag[List[Int]]
  val wtt = weakTypeTag[List[Int]]

  val array = ct.newArray(3)
  array.update(2, "tst")

  println(array.mkString(","))
  println(tt.tpe)
  println(wtt.equals(tt))

  println()
}
