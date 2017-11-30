package org.ditw.graphProb

import java.awt.{Color, Dimension}
import javax.swing.JFrame

import org.ditw.graphProb.belUpdating.GraphHelpers._
import org.jgraph.JGraph
import org.jgrapht.ListenableGraph
import org.jgrapht.ext.JGraphModelAdapter
import org.jgrapht.graph.{DefaultEdge, DefaultListenableGraph, DirectedMultigraph}

/**
  * Created by dev on 2017-11-27.
  */
object JGraphSmokeTest extends App {

//  val applet = new JGraphTestApplet
//  applet.init()
//  val frame = new JFrame
//  frame.getContentPane.add(applet)
//  frame.setTitle("JGraphT Adapter to JGraph Demo")
//  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
//  frame.pack()
//  frame.setVisible(true)

//  val conf = new GraphConfig(
//    List(
//      ("v1", 10, 10),
//      ("v2", 200, 10),
//      ("v3", 10, 200),
//      ("v4", 200, 200),
//      ("v5", 400, 200)
//    ),
//    List(
//      ("v1", "v2"),
//      ("v2", "v4"),
//      ("v3", "v4"),
//      ("v1", "v3"),
//      ("v2", "v5")
//    )
//  )

  val dummy:AnyRef = null

  val model = ProbModel(
    List(
      Potential(Set("F1", "F2"), dummy),
      Potential(Set("F1", "F3"), dummy),
      Potential(Set("F2", "F4"), dummy),
      Potential(Set("F2", "F3", "F5"), dummy),
      Potential(Set("F3", "F6"), dummy),
      Potential(Set("F5", "F6", "F7"), dummy),
      Potential(Set("F7", "F8"), dummy),
      Potential(Set("F8", "F9"), dummy),
      Potential(Set("F8", "Fa"), dummy),
      Potential(Set("F9", "Fb"), dummy)
    )
  )

  val app = new GraphApplet(model)
  app.init()

  val frame = new JFrame
  frame.getContentPane.add(app)
  frame.setTitle("JGraphT Adapter to JGraph Demo")
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.pack()
  frame.setVisible(true)
}
