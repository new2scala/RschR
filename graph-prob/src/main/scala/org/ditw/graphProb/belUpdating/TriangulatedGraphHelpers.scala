package org.ditw.graphProb.belUpdating

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-11-30.
  */
object TriangulatedGraphHelpers {
  import collection.JavaConverters._

  class VertexEdge extends DefaultEdge {
    def vertices:Array[String] = Array(getSource.toString, getTarget.toString)
  }

  def findSimplicialNodes(g:Graph[String,VertexEdge]):Set[String] = {
    val nbs = g.vertexSet().asScala.map { vtx =>
      val edges = g.edgesOf(vtx)
      vtx -> edges.asScala.map { e =>
        val vs = e.vertices
        if (vs(0) == vtx) vs(1)
        else vs(0)
      }.toSet
    }.toMap

    val r = ListBuffer[String]()
    g.vertexSet().asScala.foreach { vtx =>
      val ns = nbs(vtx)
      val t = ns.forall { e =>
        val nst = ns -- Set(e)
        nst.subsetOf(nbs(e))
      }
      if (t) r += vtx
    }
    r.toSet
  }
}
