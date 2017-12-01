package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgrapht.Graph

/**
  * Created by dev on 2017-12-01.
  */
import collection.JavaConverters._
class EnrichedGraph[E <: VertexEdge](private val _g:Graph[String, E]) {
  private val _nbMap:Map[String,Set[String]] = {
    _g.vertexSet().asScala.map { vtx =>
      val edges = _g.edgesOf(vtx)
      val nbVtx:Set[String] = edges.asScala.map { e =>
        val vs = e.vertices
        if (vs(0) == vtx) vs(1)
        else vs(0)
      }.toSet
      vtx -> nbVtx
    }.toMap
  }

  private val _vertices = _g.vertexSet().asScala.toSet

  def vertices:Set[String] = _vertices

  def neighbors(vtx:String):Set[String] = _nbMap(vtx)

  def family(vtx:String):Set[String] = neighbors(vtx)+vtx
}

