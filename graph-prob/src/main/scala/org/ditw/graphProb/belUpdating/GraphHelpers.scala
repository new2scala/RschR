package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.ProbModels.ProbModel
import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgrapht.Graph
import org.jgrapht.graph.{DefaultEdge, SimpleGraph}

import scala.reflect.{ClassTag, classTag, _}
/**
  * Created by dev on 2017-11-29.
  */
object GraphHelpers {

  case class Factor(id:String, desc:String)

  import Potentials._

  def createSimpleGraph[T <: VertexEdge : ClassTag]:SimpleGraph[String, T] =
    new SimpleGraph[String, T](classTag[T].runtimeClass.asInstanceOf[Class[T]])
  import collection.JavaConverters._
  def cloneSimpleGraph[T <: VertexEdge : ClassTag](g:SimpleGraph[String, T]):SimpleGraph[String, T] = {
    val newg = createSimpleGraph[T]
    g.vertexSet().asScala.foreach(newg.addVertex)
    g.edgeSet().asScala.foreach(ed => newg.addEdge(ed.vertices(0), ed.vertices(1)))
    newg
  }

  def graphFromModel[T <: VertexEdge : ClassTag](model:ProbModel):SimpleGraph[String, T] = {
    val vertexIds = model.potentials.flatMap(_.allIds)

    val edges = model.potentials.flatMap { p =>
      val fids = p.allIds.toArray.sorted
      val pairs = for (i <- fids.indices; j <- i+1 until fids.length) yield fids(i)-> fids(j)
      pairs
    }

    val g = createSimpleGraph[T]
    vertexIds.foreach(g.addVertex)

    edges.foreach(p => g.addEdge(p._1, p._2))
    g
  }

  private[graphProb] def _buildGraph(edges:Seq[(String,String)]):EnrichedGraph[VertexEdge] = {
    val vtxs = edges.flatMap(p => Set(p._1, p._2))
    val g = new SimpleGraph[String, VertexEdge](classOf[VertexEdge])
    vtxs.foreach(g.addVertex)
    edges.foreach(p => g.addEdge(p._1, p._2))
    val r = new EnrichedGraph[VertexEdge](g)
    r
  }

  def buildGraph(edges:(String,String)*):EnrichedGraph[VertexEdge] = _buildGraph(edges)
}
