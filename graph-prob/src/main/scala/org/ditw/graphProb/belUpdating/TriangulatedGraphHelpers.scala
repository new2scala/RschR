package org.ditw.graphProb.belUpdating

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-11-30.
  */
object TriangulatedGraphHelpers {


  class VertexEdge extends DefaultEdge {
    def vertices:Array[String] = Array(getSource.toString, getTarget.toString)
  }


  def findSimplicialNodes(g:EnrichedGraph[VertexEdge]):Set[String] = {

    val r = ListBuffer[String]()
    g.vertices.foreach { vtx =>
      val ns = g.neighbors(vtx)
      val t = ns.forall { e =>
        val nst = ns -- Set(e)
        nst.subsetOf(g.neighbors(e))
      }
      if (t) r += vtx
    }
    r.toSet
  }

}
