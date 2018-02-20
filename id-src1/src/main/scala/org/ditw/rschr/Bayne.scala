package org.ditw.rschr

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, Graph, VertexId}

/**
  * Created by dev on 2018-02-20.
  */
object Bayne {

  type NodeId = VertexId

  private val ToRel = "->"
  private val MissingNode = "N/A"

  case class Potential(n:NodeId, pars:Array[NodeId], probs:Array[Double])

  case class BayNet(potentials:Iterable[Potential]) {
    private val _nodes = potentials
      .flatMap(p => Set(p.n) ++ p.pars)
      .toArray.distinct.sorted
      .map(n => n -> s"A$n")
    private val _edges = potentials.flatMap { p =>
      p.pars.map(pp => Edge(pp, p.n, ToRel))
    }.toArray

    def toGraph(spark:SparkContext):Graph[String,String] =
      Graph(spark.parallelize(_nodes), spark.parallelize(_edges), MissingNode)

    def toDomainGraph(spark:SparkContext):Graph[String,String] = {
      val domainEdges = potentials.flatMap { p =>
        val allNodes = (IndexedSeq(p.n) ++ p.pars).sorted
        val edges = allNodes.indices.flatMap { i =>
          val ni = allNodes(i)
          (i+1 until allNodes.size).map { j =>
            Edge(ni, allNodes(j), ToRel)
          }
        }
        edges
      }
      Graph(spark.parallelize(_nodes), spark.parallelize(domainEdges.toSeq), MissingNode)
    }
  }

//  def potentials2Graph(spark:SparkContext, pots:Iterable[Potential]):Graph[String,String] = {
//    val allNodeIds = pots.flatMap(p => Set(p.n) ++ p.pars).toArray.distinct.sorted
//
//    val nodes = allNodeIds.map(nid => nid -> s"A$nid")
//
//    val edges = pots.flatMap { p =>
//      p.pars.map(pp => Edge(pp, p.n, "->"))
//    }
//
//    Graph(spark.parallelize(nodes), spark.parallelize(edges.toSeq), "N/A")
//  }
}
