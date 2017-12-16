package org.ditw.graphProb.belUpdating

/**
  * Created by dev on 2017-12-12.
  */
object Potentials {
  val dummy = null

  case class Potential(ids:Set[String], condIds:Set[String], data:PotentialData) {
    val allIds = ids ++ condIds
  }

  private[graphProb] trait TPotentialProbTreeNode {
    val vars:IndexedSeq[String]
//    val path:Option[Int]
    def isLeaf:Boolean
    // only valid for leaf nodes
    def getProb(p:Int):Double
  }

  private[graphProb] class PotentialProbTreeLeaf(
    val vars:IndexedSeq[String],
//    val path:Option[Int],
    val probs:IndexedSeq[Double]
  ) extends TPotentialProbTreeNode {
    override def isLeaf:Boolean = true
    def getProb(p:Int):Double = probs(p)
    def +(p2:IndexedSeq[Double]):PotentialProbTreeLeaf = {
      if (p2.size != probs.size) throw new IllegalArgumentException(s"Size doesn't match: ${p2.size}/${probs.size}")
      val newProbs = probs.indices.map(idx => p2(idx) + probs(idx))
      new PotentialProbTreeLeaf(vars, newProbs)
    }
  }

  private[graphProb] class PotentialProbTreeNonLeaf(
    val vars:IndexedSeq[String],
//    val path:Option[Int],
    val children:IndexedSeq[TPotentialProbTreeNode]
  ) extends TPotentialProbTreeNode {
    override def isLeaf:Boolean = false
    def getProb(p:Int):Double = throw new IllegalAccessError("Operation not supported!")
    def clone(newChildren:IndexedSeq[TPotentialProbTreeNode]):PotentialProbTreeNonLeaf = {
      new PotentialProbTreeNonLeaf(vars, newChildren)
    }
  }

  private def buildProbTree(vars:IndexedSeq[IndexedSeq[String]], probs:Array[Double])
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
          children(idx) = buildProbTree(remVars, probSlice)
        }
        new PotentialProbTreeNonLeaf(vars.head, children)
      }
    }
    else { // leaf node
      new PotentialProbTreeLeaf(vars.head, probs)
    }
  }

  // merge:
  //   n
  //   |_ l   => l
  //   |_ l
  //
  //   n          =>   n
  //   |_ n            |_ .. l1+l3
  //      |_ .. l1     |_ .. l2+l4
  //      |_ .. l2
  //   |_ n
  //      |_ .. l3
  //      |_ .. l4
  private[graphProb] def mergeSubTree(nonLeafParent:TPotentialProbTreeNode):TPotentialProbTreeNode = {
    val p = nonLeafParent.asInstanceOf[PotentialProbTreeNonLeaf]
    p.children.head match {
      case leaf:PotentialProbTreeLeaf => {
        val arr = new Array[Double](leaf.probs.size)
        p.children.foreach { c =>
          val cl = c.asInstanceOf[PotentialProbTreeLeaf]
          cl.probs.indices.foreach(idx => arr(idx) += cl.probs(idx))
        }
        val newLeaf = new PotentialProbTreeLeaf(
          nonLeafParent.vars,
          arr
        )
        newLeaf
      }
      case node:PotentialProbTreeNonLeaf => {
        val otherChildren = p.children.tail
        var r:TPotentialProbTreeNode = node
        otherChildren.foreach { oc =>
          r = addChildren(r, oc)
        }
        r
      }
      case _ => {
        throw new IllegalArgumentException(s"Unknown type of node: ${p.children.head.getClass}")
      }
    }
  }

  private def addChildren(n1:TPotentialProbTreeNode, n2:TPotentialProbTreeNode):TPotentialProbTreeNode = {
    n1 match {
      case nonLeaf:PotentialProbTreeNonLeaf => {
        val nonLeaf2 = n2.asInstanceOf[PotentialProbTreeNonLeaf]
        val addedChildren = nonLeaf.children.indices.map { idx =>
          addChildren(nonLeaf.children(idx), nonLeaf2.children(idx))
        }
        nonLeaf.clone(addedChildren)
      }
      case leaf:PotentialProbTreeLeaf => {
        leaf + n2.asInstanceOf[PotentialProbTreeLeaf].probs
      }
      case _ => {
        throw new IllegalArgumentException(s"Unknown type of node: ${n1.getClass}")
      }
    }
  }

  val BooleanVars = IndexedSeq("T", "F")
  val BooleanVars1 = IndexedSeq(BooleanVars)
  val BooleanVars2 = IndexedSeq(BooleanVars, BooleanVars)
  val BooleanVars3 = IndexedSeq(BooleanVars, BooleanVars, BooleanVars)

  case class PotentialData(vars:IndexedSeq[IndexedSeq[String]], probs:Array[Double]) {
    private val _probTree:TPotentialProbTreeNode = buildProbTree(vars, probs)
    private[graphProb] def treeRoot:TPotentialProbTreeNode = _probTree

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

//    def marginalize(indices:Set[Int]):PotentialData = {
//      var tmpVars:IndexedSeq[IndexedSeq[String]] = IndexedSeq()
//      var tmpProbs:Array[Double] = Array()
//
//      indices.foreach { idx =>
//        tmpVars = vars.slice(0, idx) ++ vars.slice(idx+1, vars.size)
//
//      }
//    }
  }
}
