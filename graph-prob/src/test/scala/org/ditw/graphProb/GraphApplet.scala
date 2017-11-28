package org.ditw.graphProb

import java.awt.{Color, Dimension}
import java.awt.geom.Rectangle2D
import java.util
import javax.swing.JApplet

import org.jgraph.JGraph
import org.jgraph.graph.{AttributeMap, GraphConstants}
import org.jgrapht.Graph
import org.jgrapht.alg.CycleDetector
import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.{DefaultEdge, DefaultListenableGraph, DirectedMultigraph, SimpleGraph}

/**
  * Created by dev on 2017-11-28.
  */
class GraphApplet(conf:GraphConfig) extends JApplet {
  private var jgAdapter:JGraphModelAdapter[String, DefaultEdge] = null

  import collection.JavaConverters._
  override def init(): Unit = {
    // create a JGraphT graph
    val g = new DefaultListenableGraph[String, DefaultEdge](
      new DirectedMultigraph[String, DefaultEdge](classOf[DefaultEdge])
    )

    // create a visualization using JGraph, via an adapter
    jgAdapter = new JGraphModelAdapter[String, DefaultEdge](g)

    val jgraph = new JGraph(jgAdapter)

    adjustDisplaySettings(jgraph)
    getContentPane.add(jgraph)
    resize(DEFAULT_SIZE)

    conf.initConf(g, this)

    testAlgs(g)
  }

  private def testAlgs(g:Graph[String, DefaultEdge]):Unit = {
    val clFinder = new BronKerboschCliqueFinder[String, DefaultEdge](g)

    val it = clFinder.iterator()
    while (it.hasNext) {
      val cl = it.next()
      println(cl.asScala.toList.mkString(","))
    }

    val cd = new CycleDetector[String, DefaultEdge](g)
    val circles = cd.findCycles()
    println(circles.size())
  }

  private def adjustDisplaySettings(jg: JGraph) = {
    jg.setPreferredSize(DEFAULT_SIZE)
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
  private val DEFAULT_SIZE = new Dimension(800, 600)

}
