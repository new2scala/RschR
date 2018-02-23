package org.ditw.rschr

import org.ditw.rschr.Bayne.{NodeValueSets, Potential, ProbDistr}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-02-20.
  */
class PotentialTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val pot1 = Potential(
    5L,
    Array(2L, 3L),
    NodeValueSets(Map(2L -> 2, 3L -> 2, 5L -> 2)),
    Array(0.1, 0.9, 0.8, 0.2, 0.3, 0.7, 0.4, 0.6)
  )

  private val pot2 = Potential(
    5L,
    Array(2L, 3L),
    NodeValueSets(Map(2L -> 3, 3L -> 3, 5L -> 2)),
    Array(
      0.1, 0.9, 0.15, 0.85, 0.2, 0.8,
      0.25, 0.75, 0.3, 0.7, 0.35, 0.65,
      0.4, 0.6, 0.45, 0.55, 0.51, 0.49
    )
  )

  private val tolerance = 1e-8

  private val potentialTestData = Table(
    ("potential", "nValIndex", "parValueIndices", "prob"),
    (pot1, 1, Array(0, 0), 0.9),
    (pot1, 1, Array(1, 0), 0.2),
    (pot1, 0, Array(1, 1), 0.4),

    (pot2, 0, Array(1, 1), 0.3),
    (pot2, 1, Array(0, 1), 0.75),
    (pot2, 1, Array(2, 0), 0.8),
    (pot2, 1, Array(2, 2), 0.49)
  )

  "Potential test" should "pass" in {
    forAll(potentialTestData) { (pot, nIdx, parIdx, expVal) =>

      val p = pot.prob(nIdx, parIdx)

      math.abs(p - expVal) shouldBe <(tolerance)
    }
  }

  private val vs = NodeValueSets(Map(1L -> 2, 2L -> 2, 3L -> 2))
  private val vs2 = NodeValueSets(Map(1L -> 2, 2L -> 2, 3L -> 2, 4L -> 2))

  private val eliminateTestData = Table(
    ("probDistrs", "node2Elim", "newDistr"),
    (
      Iterable(
        ProbDistr(
          Array(3L, 1L),
          vs,
          Array(0.9, 0.1, 0.8, 0.2)
        ),
        ProbDistr(
          Array(1L, 2L),
          vs,
          Array(0.7, 0.3, 0.6, 0.4)
        )
      ),
      1L,
      ProbDistr(
        Array(2L, 3L),
        vs,
        Array(0.87, 0.86, 0.13, 0.14)
      )
    ),
    (
      Iterable(
        ProbDistr(
          Array(3L, 4L, 1L),
          vs2,
          Array(0.05, 0.1, 0.15, 0.7, 0.2, 0.3, 0.23, 0.27)
        ),
        ProbDistr(
          Array(1L, 2L),
          vs2,
          Array(0.25, 0.3, 0.35, 0.1, 0.4, 0.45, 0.07, 0.08)
        )
      ),
      1L,
      ProbDistr(
        Array(2L, 3L, 4L),
        vs2,
        Array(
          0.05*0.25+0.2*0.75,
          0.05*0.35+0.2*0.65,
          0.1*0.25+0.3*0.75,
          0.1*0.35+0.3*0.65,
          0.15*0.25+0.23*0.75,
          0.15*0.35+0.23*0.65,
          0.7*0.25+0.27*0.75,
          0.7*0.35+0.27*0.65
        )
      )
    )
//    ,
//    (
//      Iterable(
//        ProbDistr(
//          Array(3L, 4L, 1L),
//          vs2,
//          Array(0.05, 0.1, 0.15, 0.7, 0.2, 0.3, 0.23, 0.27)
//        ),
//        ProbDistr(
//          Array(1L, 4L, 2L),
//          vs2,
//          Array(0.25, 0.3, 0.35, 0.1, 0.4, 0.45, 0.07, 0.08)
//        )
//      ),
//      1L,
//      ProbDistr(
//        Array(2L, 3L, 4L),
//        vs2,
//        Array(
//          0.25*0.05+0.2*0.3, 0.05*0.4+0.2*0.45, 0.1*0.25+0.3*0.3, 0.1*0.4+0.3*0.45,
//          0.15*0.35+0.23*0.1, 0.15*0.07+0.23*0.08, 0.7*0.35+0.27*0.1, 0.7*0.07+0.27*0.08
//        )
//      )
//    )
  )

//  "Elimination test" should "pass" in {
//    forAll(eliminateTestData) { (probDistrs, node2Elim, newDistr) =>
//      val r = Bayne.eliminate(node2Elim, probDistrs)
//      r shouldBe newDistr
//    }
//  }
}
