package org.ditw.rschr.utils

import org.apache.spark.graphx.{Edge, Graph}
import org.ditw.rschr.Bayne
import org.ditw.rschr.Bayne.{BayNet, NodeValueSets, Potential}

/**
  * Created by dev on 2018-02-20.
  */
object GraphXBN1 extends App {
  val spark = SparkUtils.sparkContextLocal()
  def constructGraph():Graph[String, String] = {
    val nodes = spark.parallelize(
      Array(
        (1L, "A1"),
        (2L, "A2"),
        (3L, "A3"),
        (4L, "A4"),
        (5L, "A5"),
        (6L, "A6")
      )
    )

    val rel = "to"
    val edges = spark.parallelize(
      Array(
        Edge(1L, 2L, rel),
        Edge(1L, 3L, rel),
        Edge(2L, 4L, rel),
        Edge(2L, 5L, rel),
        Edge(3L, 5L, rel),
        Edge(3L, 6L, rel)
      )
    )

    val graph = Graph(nodes, edges, "N/A")
    graph
  }

  private val _arrayTodo = Array[Double]()

  private val vs = NodeValueSets(
    (1L to 6L).map(nid => nid -> 2).toMap
  )

  def bnFromPotentials():BayNet = {
    val pots = Iterable(
      Potential(2, Array(1L), vs, _arrayTodo),
      Potential(3, Array(1L), vs, _arrayTodo),
      Potential(4, Array(2L), vs, _arrayTodo),
      Potential(5, Array(2L, 3L), vs, _arrayTodo),
      Potential(6, Array(3L), vs, _arrayTodo)
    )

    BayNet(pots)
  }

  val bn = bnFromPotentials()
  println("=== Bayesian Net ===")
  println(GraphUtils.traceGraph(bn.toGraph(spark)))
  println("=== Domain Graph ===")
  println(GraphUtils.traceGraph(bn.toDomainGraph(spark)))

  spark.stop()
}
