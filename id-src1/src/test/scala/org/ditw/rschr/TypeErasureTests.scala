package org.ditw.rschr

import scala.reflect.ClassTag

object TypeErasureTests extends App {

  def ext[T](l:List[Any])(implicit evd:ClassTag[T]):List[T] = l.flatMap {
    case e:T => Some(e)
    case _ => None
  }

  val l1 = List(1, "abc", 2, false)

  val res = ext[String](l1)
  println(res)

}
