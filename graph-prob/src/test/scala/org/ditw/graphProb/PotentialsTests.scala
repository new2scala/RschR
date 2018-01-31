package org.ditw.graphProb

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2017-12-12.
  */
class PotentialsTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import org.ditw.graphProb.belUpdating.Potentials._
  private val buildProbTreeTestData = Table(
    ("vars", "probs", "getProbTests"),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
      List(
        IndexedSeq(0, 0, 0) -> 0.95,
        IndexedSeq(0, 0, 1) -> 0.05,
        IndexedSeq(0, 1, 0) -> 0.9,
        IndexedSeq(0, 1, 1) -> 0.1,
        IndexedSeq(1, 0, 0) -> 0.8,
        IndexedSeq(1, 0, 1) -> 0.2,
        IndexedSeq(1, 1, 0) -> 0.0,
        IndexedSeq(1, 1, 1) -> 1.0
      )
    ),
    (
      BooleanVars2,
      Array(0.2, 0.8, 0.75, 0.25),
      List(
        IndexedSeq(0, 0) -> 0.2,
        IndexedSeq(0, 1) -> 0.8,
        IndexedSeq(1, 0) -> 0.75,
        IndexedSeq(1, 1) -> 0.25
      )
    ),
    (
      BooleanVars1,
      Array(0.6, 0.4),
      List(
        IndexedSeq(0) -> 0.6,
        IndexedSeq(1) -> 0.4
      )
    )
  )

  val doubleTolerance = 1E-9

  "buildProbTree test" should "pass" in {
    forAll(buildProbTreeTestData) { (vars, probs, getProbTests) =>
      val t = genPotentialData(vars, probs)
      getProbTests.foreach { p =>
        val diff = Math.abs(t.getProb(p._1) - p._2)
        diff shouldBe <(doubleTolerance)
      }
    }
  }

  private val mergeSubTreeTestData = Table(
    ("vars", "probs", "mergedVars", "mergedProbs"),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
//      List(
//        IndexedSeq(0, 0, 0) -> 0.95,
//        IndexedSeq(0, 0, 1) -> 0.05,
//        IndexedSeq(0, 1, 0) -> 0.9,
//        IndexedSeq(0, 1, 1) -> 0.1,
//        IndexedSeq(1, 0, 0) -> 0.8,
//        IndexedSeq(1, 0, 1) -> 0.2,
//        IndexedSeq(1, 1, 0) -> 0.0,
//        IndexedSeq(1, 1, 1) -> 1.0
//      )
      BooleanVars2,
      Array(1.75, 0.25, 0.9, 1.1)
    ),
    (
      BooleanVars2,
      Array(0.2, 0.8, 0.75, 0.25),
      BooleanVars1,
      Array(0.95, 1.05)
    )
  )

  def compareTree(t1:TPotentialProbTreeNode, t2:TPotentialProbTreeNode):Boolean = {
    if (t1.isInstanceOf[PotentialProbTreeLeaf]) {
      val l1 = t1.asInstanceOf[PotentialProbTreeLeaf]
      val l2 = t2.asInstanceOf[PotentialProbTreeLeaf]

      l1.vars == l2.vars &&
        l1.probs.indices.forall { idx =>
          math.abs(l1.probs(idx) - l2.probs(idx)) < doubleTolerance
        }
    }
    else {
      val n1 = t1.asInstanceOf[PotentialProbTreeNonLeaf]
      val n2 = t2.asInstanceOf[PotentialProbTreeNonLeaf]

      n1.vars == n2.vars &&
        n1.children.indices.forall { idx =>
          compareTree(n1.children(idx), n2.children(idx))
        }
    }
  }

  "mergeSubTree test" should "pass" in {
    forAll(mergeSubTreeTestData) { (vars, probs, mergedVars, mergedProbs) =>
      val t = genPotentialData(vars, probs)
      val m = mergeSubTree(t.treeRoot)

      var t2 = genPotentialData(mergedVars, mergedProbs)

      compareTree(m, t2.treeRoot) shouldBe true
    }
  }

  private val marginalizeTestData = Table(
    ("vars", "probs", "marginalizeIndices", "mergedVars", "mergedProbs"),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
      Set(1, 2),
      BooleanVars1,
      Array(2.0, 2.0)
    ),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
      Set(1),
      BooleanVars2,
      Array(1.85, 0.15, 0.8, 1.2)
    ),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
      Set(0, 2),
      BooleanVars1,
      Array(2.65, 1.35)
    ),
    (
      BooleanVars3,
      Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0, 1),
      Set(0),
      BooleanVars2,
      Array(1.75, 0.25, 0.9, 1.1)
    ),
    (
      BooleanVars2,
      Array(0.2, 0.8, 0.75, 0.25),
      Set(0),
      BooleanVars1,
      Array(0.95, 1.05)
    )
  )

  "marginalize test" should "pass" in {
    forAll(marginalizeTestData) { (vars, probs, marginalizeIndices, mergedVars, mergedProbs) =>
      val t = genPotentialData(vars, probs)
      val m = t.marginalize(marginalizeIndices)

      var t2 = genPotentialData(mergedVars, mergedProbs)

      compareTree(m.treeRoot, t2.treeRoot) shouldBe true
    }
  }
}
