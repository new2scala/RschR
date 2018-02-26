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
    def valIndexOf(nid:NodeId):IndexedSeq[Int] = 0 until vm(nid)
    def valIndicesOf(nids:Seq[NodeId]):IndexedSeq[IndexedSeq[Int]] = {
      val r:mutable.IndexedSeq[IndexedSeq[Int]] = mutable.IndexedSeq(IndexedSeq())
      nids.foldLeft(r)(
        (sseq, nid) => {
          sseq.flatMap(seq => valIndexOf(nid).map(seq :+ _))
        }
      )
    }
    def valReverseIndicesOf(nids:Seq[NodeId]):IndexedSeq[IndexedSeq[Int]] = {
      val r:mutable.IndexedSeq[IndexedSeq[Int]] = mutable.IndexedSeq(IndexedSeq())
      nids.foldRight(r)(
        (nid, sseq) => {
          sseq.flatMap(seq => valIndexOf(nid).map(_ +: seq))
        }
      )
    }
  }

  private val tolerance = 1e-8

  //// prob example, note the "least important bit" is the leftmost one
  //   A  B  C  D  P(A|B,C,D)
  //   0  0  0  0       0.12
  //   1  0  0  0       0.88
  //   0  1  0  0       0.20
  //   1  1  0  0       0.80
  //   0  0  1  0       0.28
  //   1  0  1  0       0.72
  //   0  1  1  0       0.36
  //   1  1  1  0       0.64
  //   0  0  0  1       0.16
  //   1  0  0  1       0.84
  //   0  1  0  1       0.24
  //   1  1  0  1       0.76
  //   0  0  1  1       0.32
  //   1  0  1  1       0.68
  //   0  1  1  1       0.40
  //   1  1  1  1       0.60
  case class ProbDistr(nodes:IndexedSeq[NodeId], vs:NodeValueSets, probs:IndexedSeq[Double]) {

    override def hashCode(): Int = nodes.hashCode() + vs.hashCode() + probs.hashCode()

    override def equals(obj: scala.Any): Boolean = {
      obj match {
        case pd:ProbDistr => {
          vs.vm == pd.vs.vm && nodes == pd.nodes &&
            probs.indices.forall(idx => math.abs(probs(idx) - pd.probs(idx)) <= tolerance)
        }
        case _ => false
      }
    }

    def prob(valueIndices:IndexedSeq[Int]):Double = {
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

    def probsOf(nids:Seq[NodeId]):IndexedSeq[ProbDistr] = {
      val idxMap = mutable.Map[Int, Int]()
      val valIndices = vs.valReverseIndicesOf(nids)
      nids.indices.foreach { vidx =>
        idxMap += vidx -> nodes.indexOf(nids(vidx))
      }

      val remNodeIndices = nodes.indices.filter(idx => !nids.contains(nodes(idx)))

      val remNodes = remNodeIndices.map(nodes)
      var i = nids.size
      remNodes.foreach { remNode =>
        idxMap += i -> nodes.indexOf(remNode)
        i += 1
      }
      val indiceMap = idxMap.toList.sortBy(_._2).map(_._1).toIndexedSeq
      val remValIndices = vs.valReverseIndicesOf(remNodes)

      valIndices.map { valIndex =>
        val ps = remValIndices.map { remValIdx =>
          val vals = valIndex ++ remValIdx
          val transVals = indiceMap.map(vals)
          prob(transVals)
        }

        ProbDistr(remNodes, vs, ps)
      }
//      val nidx = nodes.indexOf(nid)
//      if (nidx < 0) throw new IllegalArgumentException(s"Unknown node id [$nid]")
//
//      val nVals = vs.vm(nid)
//      (0 until nVals).map { nVal =>
//        val valIndices1 =  remValueIndices.slice(0, nidx).toIndexedSeq
//        val valIndices2 = remValueIndices.slice(nidx+1, remValueIndices.length)
//        val valIndices = valIndices1 ++ IndexedSeq(nVal) ++ valIndices2
//        prob(valIndices)
//      }
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

  case class Potential(n:NodeId, pars:IndexedSeq[NodeId], vs:NodeValueSets, probs:IndexedSeq[Double]) {
    private val _prob = ProbDistr(n +: pars, vs, probs)
    def prob(nValueIdx:Int, parsValueIndices:IndexedSeq[Int]):Double = {
      _prob.prob(nValueIdx +: parsValueIndices)
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
