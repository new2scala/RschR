package org.ditw.rschr

import org.ditw.rschr.Bayne.{NodeValueSets, Potential}
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

  private val testData = Table(
    ("potential", "nValIndex", "parValueIndices", "prob"),
    (pot1, 1, Array(0, 0), 0.9),
    (pot1, 1, Array(1, 0), 0.2),
    (pot1, 0, Array(1, 1), 0.4),

    (pot2, 0, Array(1, 1), 0.3),
    (pot2, 1, Array(0, 1), 0.75),
    (pot2, 1, Array(2, 0), 0.8),
    (pot2, 1, Array(2, 2), 0.49)
  )

  "test" should "pass" in {
    forAll(testData) { (pot, nIdx, parIdx, expVal) =>

      val p = pot.prob(nIdx, parIdx)

      math.abs(p - expVal) shouldBe <(tolerance)
    }
  }
}
