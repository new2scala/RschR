package org.ditw.graphProb

import java.awt.{Color, Dimension}
import java.awt.geom.Rectangle2D
import java.util
import javax.swing.JApplet

import org.ditw.graphProb.belUpdating.GraphHelpers._
import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgraph.JGraph
import org.jgraph.graph.{AttributeMap, GraphConstants}
import org.jgrapht.Graph
import org.jgrapht.alg.CycleDetector
import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.{DefaultEdge, DefaultListenableGraph, DirectedMultigraph, SimpleGraph}
import sun.security.provider.certpath.Vertex

/**
  * Created by dev on 2017-11-28.
  */
class GraphApplet(model:ProbModel) extends JApplet {
  private var jgAdapter:JGraphModelAdapter[String, VertexEdge] = null

  import collection.JavaConverters._
  override def init(): Unit = {
    // create a JGraphT graph
//    val g = new DefaultListenableGraph[String, DefaultEdge](
//      new DirectedMultigraph[String, DefaultEdge](classOf[DefaultEdge])
//    )
    val g:Graph[String, VertexEdge] = graphFromModel(model)

    // create a visualization using JGraph, via an adapter
    jgAdapter = new JGraphModelAdapter[String, VertexEdge](g)

    val jgraph = new JGraph(jgAdapter)

    adjustDisplaySettings(jgraph)
    getContentPane.add(jgraph)
    resize(DEFAULT_DIM)

    initConf(g, DEFAULT_SIZE)

    //testAlgs(g)
  }

  def initConf(g:Graph[String,VertexEdge], canvasSize:Int):Unit = {

    val vtxCount = g.vertexSet().size()

    val coords = GraphLayoutHelpers.calcCoords(vtxCount, canvasSize)

    // position vertices nicely within JGraph component
    val sortedVertice = g.vertexSet().asScala.toArray.sorted
    sortedVertice.indices.foreach(idx =>
      positionVertexAt(sortedVertice(idx), coords(idx)._1, coords(idx)._2)
    )
  }

  private def testAlgs(g:Graph[String, VertexEdge]):Unit = {
    val clFinder = new BronKerboschCliqueFinder[String, VertexEdge](g)

    val it = clFinder.iterator()
    while (it.hasNext) {
      val cl = it.next()
      println(cl.asScala.toList.mkString(","))
    }

    val cd = new CycleDetector[String, VertexEdge](g)
    val circles = cd.findCycles()
    println(circles.size())
  }

  private def adjustDisplaySettings(jg: JGraph) = {
    jg.setPreferredSize(DEFAULT_DIM)
    var c = DEFAULT_BG_COLOR
    var colorStr:String = null
    try
      colorStr = getParameter("bgcolor")
    catch {
      case e: Exception =>
    }
    if (colorStr != null) c = Color.decode(colorStr)
    jg.setBackground(c)
  }

  @SuppressWarnings(Array("unchecked")) private[graphProb] def positionVertexAt(vertex: Any, x: Int, y: Int) = {
    val cell = jgAdapter.getVertexCell(vertex)
    val attr = cell.getAttributes
    val bounds = GraphConstants.getBounds(attr)
    val newBounds = new Rectangle2D.Double(x, y, bounds.getWidth, bounds.getHeight)
    GraphConstants.setBounds(attr, newBounds)
    val m = new util.HashMap[Object,Object]()
    m.put(cell, attr)
    val cellAttr = new AttributeMap(m)
    //cellAttr.put(cell, attr)
    jgAdapter.edit(cellAttr, null, null, null)
  }

  private val DEFAULT_BG_COLOR = Color.decode("#FAFBFF")
  private val DEFAULT_SIZE = 800
  private val DEFAULT_DIM = new Dimension(DEFAULT_SIZE, DEFAULT_SIZE)

}
