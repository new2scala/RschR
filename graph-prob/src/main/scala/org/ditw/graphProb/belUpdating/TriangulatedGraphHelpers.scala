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

//    override def hashCode(): Int = vertices(0).hashCode + vertices(1).hashCode
//
//    override def equals(obj: scala.Any): Boolean = obj match {
//      case v:VertexEdge => vertices.sameElements(v.vertices)
//      case _ => false
//    }
  }




}
