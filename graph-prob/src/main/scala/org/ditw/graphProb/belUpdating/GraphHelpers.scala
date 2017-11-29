package org.ditw.graphProb.belUpdating

import org.jgrapht.Graph
import org.jgrapht.graph.{DefaultEdge, SimpleGraph}

/**
  * Created by dev on 2017-11-29.
  */
object GraphHelpers {

  case class Factor(id:String, desc:String)
  case class Potential(factorIds:Set[String], data:AnyRef)

  case class ProbModel(potentials:List[Potential], desc:String = "")

  def graphFromModel(model:ProbModel):Graph[String, DefaultEdge] = {
    val vertexIds = model.potentials.flatMap(_.factorIds)

    val edges = model.potentials.flatMap { p =>
      val fids = p.factorIds.toArray.sorted
      val pairs = for (i <- fids.indices; j <- i+1 until fids.length) yield fids(i)-> fids(j)
      pairs
    }

    val g = new SimpleGraph[String, DefaultEdge](classOf[DefaultEdge])
    vertexIds.foreach(g.addVertex)

    edges.foreach(p => g.addEdge(p._1, p._2))
    g
  }
}
