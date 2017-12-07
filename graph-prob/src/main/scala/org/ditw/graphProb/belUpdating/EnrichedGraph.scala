package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.GraphHelpers.cloneSimpleGraph
import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgrapht.Graph
import org.jgrapht.graph.SimpleGraph

import scala.collection.mutable.ListBuffer
import scala.reflect.{ClassTag, classTag}

/**
  * Created by dev on 2017-12-01.
  */
import collection.JavaConverters._
class EnrichedGraph[E <: VertexEdge : ClassTag](private val _g:SimpleGraph[String, E]) {
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
  private val _edges:Set[E] = _g.edgeSet().asScala.toSet
  def edges:Set[E] = _edges

  def neighbors(vtx:String):Set[String] = _nbMap(vtx)

  def family(vtx:String):Set[String] = neighbors(vtx)+vtx

  private def testSimplicial(vtx:String):Boolean = {
    val ns = neighbors(vtx)
    ns.forall { e =>
      val nst = ns -- Set(e)
      nst.subsetOf(neighbors(e))
    }
  }

  def simplicialNodes:Set[String] = vertices.filter(testSimplicial)

  def cloneGraph:EnrichedGraph[E] = {
    val g = cloneSimpleGraph(_g)
    new EnrichedGraph(g)
  }

  def removeVertex(vtx:String):Boolean = _g.removeVertex(vtx)

  private[graphProb] def _eliminateOne:Option[EnrichedGraph[E]] = {
    val itVtxs = _vertices.toList.sorted.iterator
    var r:Option[EnrichedGraph[E]] = None
    while (r.isEmpty && itVtxs.hasNext) {
      val vtx = itVtxs.next()
      if (testSimplicial(vtx)) {
        val g1 = cloneSimpleGraph(_g)
        g1.removeVertex(vtx)
        r = Option(new EnrichedGraph(g1))
      }
    }
    r
  }
}

