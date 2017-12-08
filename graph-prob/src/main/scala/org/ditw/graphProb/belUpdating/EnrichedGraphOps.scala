package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/**
  * Created by dev on 2017-12-07.
  */
object EnrichedGraphOps {

  private[graphProb] def add2CliqueSet(curr:ListBuffer[Set[String]], newCandidate:Set[String]):ListBuffer[Set[String]] = {
    var done = false
    var shouldAdd = true
    val it = curr.iterator
    var res = ListBuffer[Set[String]]()
    while (!done && it.hasNext) {
      val c = it.next()
      if (!c.subsetOf(newCandidate)) {
        if (newCandidate.subsetOf(c)) {
          shouldAdd = false
          done = true
        }
        res += c
      }

    }
    while (it.hasNext) {
      res += it.next()
    }
    if (shouldAdd) res += newCandidate
    res
  }

  private[graphProb] def removeSimplicialNodesFromClique[E <: VertexEdge : ClassTag](simplicialVtx:String, eg:EnrichedGraph[E])
    :(Set[String], Set[String], EnrichedGraph[E]) = {
    val cl = eg.family(simplicialVtx)
    val simpNodesInCl = cl.filter(eg.testSimplicial)
    val g1 = GraphHelpers.cloneSimpleGraph(eg._g)
    simpNodesInCl.foreach(g1.removeVertex)
    val newg = new EnrichedGraph[E](g1)
    (cl, simpNodesInCl, newg)
  }

  class EnrichedGraphOps[E <: VertexEdge : ClassTag](private val _eg:EnrichedGraph[E]) {
    def findCliques:Set[Set[String]] = {
      var res = ListBuffer[Set[String]]()
      var next = _eg._eliminateOne
      var eg = _eg
      while (next.nonEmpty) {
        val (vtx, g1) = next.get
        res = add2CliqueSet(res, eg.family(vtx))
        eg = g1
        next = eg._eliminateOne
      }
      res.toSet
    }

    private[graphProb] def prepareJoinTree:IndexedSeq[(Set[String], Set[String])] = {
      var res = ListBuffer[(Set[String], Set[String])]()
      var next = _eg.firstSimplicialNode
      var eg = _eg
      while (next.nonEmpty) {
        val vtx = next.get
        val (cl, removed, g1) = removeSimplicialNodesFromClique(vtx, eg)
        res += cl -> (cl -- removed)
        eg = g1
        next = eg.firstSimplicialNode
      }
      res.toIndexedSeq
    }

    private[graphProb] def _genJoinTree:(IndexedSeq[(Set[String], Set[String])], List[(Int, Int)]) = {
      val l = prepareJoinTree

      val links = ListBuffer[(Int, Int)]()
      l.indices.foreach { idx =>
        var start = idx+1
        var found = false
        val (cli, si) = l(idx)
        while (!found && start < l.size) {
          val (cl, s) = l(start)
          if (si.subsetOf(cl)) {
            val c = si -- s
            if (c.nonEmpty) {
              found = true
              links += idx -> start
            }
          }
          start = start + 1
        }
      }

      l -> links.toList
    }
  }

  implicit def enrichedGraph2Ops[E <: VertexEdge : ClassTag](eg:EnrichedGraph[E]):EnrichedGraphOps[E] =
    new EnrichedGraphOps(eg)
}
