package org.ditw.graphProb

import java.awt.geom.Rectangle2D
import java.awt.{Color, Dimension}
import java.util
import javax.swing.{JApplet, JFrame}

import org.jgraph.JGraph
import org.jgraph.graph.{AttributeMap, DefaultGraphCell, GraphConstants}
import org.jgrapht.ListenableGraph
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.{DefaultEdge, DefaultListenableGraph, DirectedMultigraph}

/**
  * Created by dev on 2017-11-27.
  */
class JGraphTestApplet extends JApplet {
  private var jgAdapter:JGraphModelAdapter[String, DefaultEdge] = null

  override def init(): Unit = {
    // create a JGraphT graph
    val g = new DefaultListenableGraph[String, DefaultEdge](new DirectedMultigraph[String, DefaultEdge](classOf[DefaultEdge]))

    // create a visualization using JGraph, via an adapter
    jgAdapter = new JGraphModelAdapter[String, DefaultEdge](g)

    val jgraph = new JGraph(jgAdapter)

    adjustDisplaySettings(jgraph)
    getContentPane.add(jgraph)
    resize(DEFAULT_SIZE)

    val v1 = "v1"
    val v2 = "v2"
    val v3 = "v3"
    val v4 = "v4"

    // add some sample data (graph manipulated via JGraphT)
    g.addVertex(v1)
    g.addVertex(v2)
    g.addVertex(v3)
    g.addVertex(v4)

    g.addEdge(v1, v2)
    g.addEdge(v2, v3)
    g.addEdge(v3, v1)
    g.addEdge(v4, v3)

    // position vertices nicely within JGraph component
    positionVertexAt(v1, 130, 40)
    positionVertexAt(v2, 60, 200)
    positionVertexAt(v3, 310, 230)
    positionVertexAt(v4, 380, 70)

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

  @SuppressWarnings(Array("unchecked")) private def positionVertexAt(vertex: Any, x: Int, y: Int) = {
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

  private val serialVersionUID = 3256444702936019250L

  private val DEFAULT_BG_COLOR = Color.decode("#FAFBFF")
  private val DEFAULT_SIZE = new Dimension(530, 320)

}
