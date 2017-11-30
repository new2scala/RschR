package org.ditw.graphProb.belUpdating

import org.jgrapht.Graph
import org.jgrapht.graph.{DefaultEdge, SimpleGraph}

import scala.reflect.ClassTag
import scala.reflect._
/**
  * Created by dev on 2017-11-29.
  */
object GraphHelpers {

  case class Factor(id:String, desc:String)
  case class Potential(factorIds:Set[String], data:AnyRef)

  case class ProbModel(potentials:List[Potential], desc:String = "")

  def graphFromModel[T <: DefaultEdge : ClassTag](model:ProbModel):Graph[String, T] = {
    val vertexIds = model.potentials.flatMap(_.factorIds)

    val edges = model.potentials.flatMap { p =>
      val fids = p.factorIds.toArray.sorted
      val pairs = for (i <- fids.indices; j <- i+1 until fids.length) yield fids(i)-> fids(j)
      pairs
    }

    val g = new SimpleGraph[String, T](classTag[T].runtimeClass.asInstanceOf[Class[T]])
    vertexIds.foreach(g.addVertex)

    edges.foreach(p => g.addEdge(p._1, p._2))
    g
  }
}
