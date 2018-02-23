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

  type ChildParentPair = (NodeId, Array[NodeId])
  type ChildParentProbs = (ChildParentPair, Array[Double])

  import collection.mutable

  case class NodeValueSets(vm:Map[NodeId, Int]) {
    def valSet(nid:NodeId):IndexedSeq[Int] = 0 until vm(nid)
    def valSets(nids:Seq[NodeId]):IndexedSeq[IndexedSeq[Int]] = {
      val r:mutable.IndexedSeq[IndexedSeq[Int]] = mutable.IndexedSeq(IndexedSeq())
      nids.foldLeft(r)(
        (sseq, nid) => {
          sseq.flatMap(seq => valSet(nid).map(seq :+ _))
        }
      )
    }
  }

  case class ProbDistr(nodes:Array[NodeId], vs:NodeValueSets, probs:Array[Double]) {
    def prob(valueIndices:Array[Int]):Double = {
      var idx = valueIndices(0)
      var dim = vs.vm(nodes(0))
      (1 until nodes.length-1).foreach { parIdx =>
        val parNode = nodes(parIdx)
        val parValueIdx = valueIndices(parIdx)
        val parDim = vs.vm(parNode)
        idx += parValueIdx*dim
        dim *= parDim
      }
      idx += valueIndices.last*dim
      probs(idx)
    }

    def probsOf(nid:NodeId, remValueIndices:Array[Int]):Array[Double] = {
      val nidx = nodes.indexOf(nid)
      if (nidx < 0) throw new IllegalArgumentException(s"Unknown node id [$nid]")

      val nVals = vs.vm(nid)
      (0 until nVals).map { nVal =>
        val valIndices1 =  remValueIndices.slice(0, nidx).toIndexedSeq
        val valIndices2 = remValueIndices.slice(nidx+1, remValueIndices.length)
        val valIndices = valIndices1 ++ IndexedSeq(nVal) ++ valIndices2
        prob(valIndices.toArray)
      }.toArray
    }

//    def probsOf(nids:Array[NodeId]):Array[Array[Double]] = {
////      val nidx = nodes.indexOf(nid)
////      if (nidx < 0) throw new IllegalArgumentException(s"Unknown node id [$nid]")
//
//      val nodeIdxs = nids.map(nodes.indexOf)
//      if (nodeIdxs.contains(-1)) throw new IllegalArgumentException(s"Unknown node id found!")
//
//      val remIdxs = nodes.indices.filter(x => !nodeIdxs.contains(x)).map(nodes)
//
//    }
  }

  case class Potential(n:NodeId, pars:Array[NodeId], vs:NodeValueSets, probs:Array[Double]) {
    private val _prob = ProbDistr(Array(n) ++ pars, vs, probs)
    def prob(nValueIdx:Int, parsValueIndices:Array[Int]):Double = {
      _prob.prob(Array(nValueIdx) ++ parsValueIndices)
    }
  }

  def createBayNet(child2ParentsProbs:Iterable[ChildParentProbs], vm:Map[NodeId, Int]): BayNet = {
    val nodeValueSets = NodeValueSets(vm)

    val pots = child2ParentsProbs.map { c2p =>
      val (p, probs) = c2p
      Potential(p._1, p._2, nodeValueSets, probs)
    }

    BayNet(pots)
  }

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

//  private def _eliminateOne(n:NodeId, prob1:ProbDistr, prob2:ProbDistr):ProbDistr = {
//    val idx1 = prob1.nodes.indexOf(n)
//    val idx2 = prob2.nodes.indexOf(n)
//    if (idx1 < 0 || idx2 < 0) throw new IllegalArgumentException(s"Node [$n] not found in at least one prob distributions")
//
//    val commonNodeIds = prob1.nodes.toSet.intersect(prob2.nodes.toSet).toArray.sorted
//
//    val nodeIdxs1 = commonNodeIds.map(prob1.nodes.indexOf)
//    val nodeIdxs2 = commonNodeIds.map(prob2.nodes.indexOf)
//
//  }
//
//  def eliminate(n:NodeId, probs:Iterable[ProbDistr]):ProbDistr = {
//
//  }

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
