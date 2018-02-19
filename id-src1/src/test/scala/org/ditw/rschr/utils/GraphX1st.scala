package org.ditw.rschr.utils

import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

/**
  * Created by dev on 2018-02-19.
  */
object GraphX1st extends App {

  val spark = SparkUtils.sparkContextLocal()

  val users:RDD[(VertexId, (String, String))] = spark.parallelize(
    Array(
      (3L, ("rxin", "student")),
      (5L, ("franklin", "prof")),
      (2L, ("istoica", "prof")),
      (7L, ("jgonzal", "postdoc"))
    )
  )

  val rels:RDD[Edge[String]] = spark.parallelize(
    Array(
      Edge(3L, 7L, "collab"), Edge(5L, 3L, "advisor"),
      Edge(2L, 5L, "colleague"),
      Edge(4L, 0L, "student") // note: vertices with id 4L and 0L does not exist
    )
  )

  val graph = Graph(
    users, rels, ("N/A", "N/A")
  )

  println(graph.toString)

  val edgesFrom5 = graph.edges.filter(_.srcId == 5L).collect()
  val edgesTo5 = graph.edges.filter(_.dstId == 5L).collect()

  //val ccGraph = graph.connectedComponents()

  val checkedGraph = graph.subgraph(vpred = (id, attr) => attr._1 != "N/A")

  checkedGraph.triplets.map(
    triplet => triplet.srcAttr._1 + " is the " + triplet.attr + " of " + triplet.dstAttr._1
  ).collect.foreach(println(_))


  println(edgesFrom5)
  println(edgesTo5)


  spark.stop()

}
