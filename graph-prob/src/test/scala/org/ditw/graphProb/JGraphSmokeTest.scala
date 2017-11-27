package org.ditw.graphProb

import java.awt.{Color, Dimension}
import javax.swing.JFrame

import org.jgraph.JGraph
import org.jgrapht.ListenableGraph
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.{DefaultEdge, DefaultListenableGraph, DirectedMultigraph}

/**
  * Created by dev on 2017-11-27.
  */
object JGraphSmokeTest extends App {

  val applet = new JGraphTestApplet
  applet.init()
  val frame = new JFrame
  frame.getContentPane.add(applet)
  frame.setTitle("JGraphT Adapter to JGraph Demo")
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.pack()
  frame.setVisible(true)

}
