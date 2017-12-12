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
      val t = PotentialData(vars, probs)
      getProbTests.foreach { p =>
        val diff = Math.abs(t.getProb(p._1) - p._2)
        diff shouldBe <(doubleTolerance)
      }
    }
  }
}
