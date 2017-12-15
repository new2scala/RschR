package org.ditw.graphProb.belUpdating

/**
  * Created by dev on 2017-12-12.
  */
object Potentials {
  val dummy = null

  case class Potential(ids:Set[String], condIds:Set[String], data:PotentialData) {
    val allIds = ids ++ condIds
  }

  private trait TPotentialProbTreeNode {
    val vars:IndexedSeq[IndexedSeq[String]]
    val path:IndexedSeq[Int]
    def isLeaf:Boolean
    // only valid for leaf nodes
    def getProb(p:Int):Double
  }

  private class PotentialProbTreeLeaf(
    val vars:IndexedSeq[IndexedSeq[String]],
    val path:IndexedSeq[Int],
    val probs:IndexedSeq[Double]
  ) extends TPotentialProbTreeNode {
    override def isLeaf:Boolean = true
    def getProb(p:Int):Double = probs(p)
  }

  private class PotentialProbTreeNonLeaf(
    val vars:IndexedSeq[IndexedSeq[String]],
    val path:IndexedSeq[Int],
    val children:IndexedSeq[TPotentialProbTreeNode]
  ) extends TPotentialProbTreeNode {
    override def isLeaf:Boolean = false
    def getProb(p:Int):Double = throw new IllegalAccessError("Operation not supported!")
  }

  private def buildProbTree(vars:IndexedSeq[IndexedSeq[String]], pathSoFar:IndexedSeq[Int], probs:Array[Double])
    :TPotentialProbTreeNode = {
    if (vars.size < 1) {
      throw new IllegalArgumentException("Requires at least one variable")
    }
    if (vars.size > 1) {
      val varValues = vars.head
      if (probs.length % varValues.size != 0)
        throw new IllegalArgumentException("Wrong dimension")
      else {
        val probSliceSize = probs.length / varValues.size
        val children = new Array[TPotentialProbTreeNode](varValues.size)
        val remVars = vars.tail
        varValues.indices.foreach { idx =>
          val probSlice = probs.slice(idx*probSliceSize, (idx+1)*probSliceSize)
          val newPathSoFar:IndexedSeq[Int] = pathSoFar ++ IndexedSeq(idx)
          children(idx) = buildProbTree(remVars, newPathSoFar, probSlice)
        }
        new PotentialProbTreeNonLeaf(remVars, pathSoFar, children)
      }
    }
    else { // leaf node
      new PotentialProbTreeLeaf(vars, pathSoFar, probs)
    }
  }

  val BooleanVars = IndexedSeq("T", "F")
  val BooleanVars1 = IndexedSeq(BooleanVars)
  val BooleanVars2 = IndexedSeq(BooleanVars, BooleanVars)
  val BooleanVars3 = IndexedSeq(BooleanVars, BooleanVars, BooleanVars)

  case class PotentialData(vars:IndexedSeq[IndexedSeq[String]], probs:Array[Double]) {
    private val _probTree:TPotentialProbTreeNode = buildProbTree(vars, IndexedSeq[Int](), probs)

    def getProb(path:IndexedSeq[Int]):Double = {
      var pi = 0
      var n = _probTree
      while (pi < path.length-1) {
        val p = path(pi)
        n = n match {
          case tn:PotentialProbTreeNonLeaf => tn.children(p)
          case _ => throw new IllegalArgumentException("Non-leaf node expected")
        }
        pi = pi+1
      }
      n match {
        case tl:PotentialProbTreeLeaf => tl.probs(path.last)
        case _ => throw new IllegalArgumentException("Non-leaf node expected")
      }
    }
  }
}
