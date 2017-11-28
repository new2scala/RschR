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

  val conf = new GraphConfig(
    List(
      ("v1", 300, 10),
      ("v2", 200, 200),
      ("v3", 400, 200),
      ("v4", 100, 400),
      ("v5", 300, 400),
      ("v6", 500, 400)
    ),
    List(
      ("v1", "v2"),
      ("v1", "v3"),
      ("v2", "v3"),
      ("v2", "v4"),
      ("v2", "v5"),
      ("v3", "v5"),
      ("v3", "v6")
    )
  )

  val app = new GraphApplet(conf)
  app.init()

  val frame = new JFrame
  frame.getContentPane.add(app)
  frame.setTitle("JGraphT Adapter to JGraph Demo")
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.pack()
  frame.setVisible(true)
}
