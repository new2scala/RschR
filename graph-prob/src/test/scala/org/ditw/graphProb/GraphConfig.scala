package org.ditw.graphProb

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge

/**
  * Created by dev on 2017-11-28.
  */
class GraphConfig(
                 vertices:List[(String, Int, Int)],
                 edges:List[(String,String)]
                 ) {
  val coords:Map[String, (Int, Int)] = vertices.map(p => p._1 -> (p._2, p._3)).toMap

  def initConf(g:Graph[String,DefaultEdge], applet:GraphApplet):Unit = {
    val v1 = "v1"
    val v2 = "v2"
    val v3 = "v3"
    val v4 = "v4"

    // add some sample data (graph manipulated via JGraphT)
    vertices.foreach(p => g.addVertex(p._1))
    edges.foreach(p => g.addEdge(p._1, p._2))

    // position vertices nicely within JGraph component
    vertices.foreach(p => applet.positionVertexAt(p._1, p._2, p._3))
  }
}
