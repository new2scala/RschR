package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge

import scala.collection.mutable.ListBuffer

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

  class EnrichedGraphOps[E <: VertexEdge](private val _eg:EnrichedGraph[E]) {
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
  }

  implicit def enrichedGraph2Ops[E <: VertexEdge](eg:EnrichedGraph[E]):EnrichedGraphOps[E] =
    new EnrichedGraphOps(eg)
}
