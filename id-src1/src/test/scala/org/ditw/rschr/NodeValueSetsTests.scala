package org.ditw.rschr

import org.ditw.rschr.Bayne.NodeValueSets
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-02-23.
  */
class NodeValueSetsTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val vs = NodeValueSets(
    Map(1L -> 2, 2L -> 3, 3L -> 4, 4L -> 2)
  )
  private val td = Table(
    ("valueSets", "nids", "resultValues"),
    (
      vs, Seq(1L),
      IndexedSeq(
        IndexedSeq(0),
        IndexedSeq(1)
      )
    ),
    (
      vs, Seq(1L, 2L),
      IndexedSeq(
        IndexedSeq(0, 0),
        IndexedSeq(0, 1),
        IndexedSeq(0, 2),
        IndexedSeq(1, 0),
        IndexedSeq(1, 1),
        IndexedSeq(1, 2)
      )
    ),
    (
      vs, Seq(2L, 1L),
      IndexedSeq(
        IndexedSeq(0, 0),
        IndexedSeq(0, 1),
        IndexedSeq(1, 0),
        IndexedSeq(1, 1),
        IndexedSeq(2, 0),
        IndexedSeq(2, 1)
      )
    ),
    (
      vs, Seq(1L, 3L),
      IndexedSeq(
        IndexedSeq(0, 0),
        IndexedSeq(0, 1),
        IndexedSeq(0, 2),
        IndexedSeq(0, 3),
        IndexedSeq(1, 0),
        IndexedSeq(1, 1),
        IndexedSeq(1, 2),
        IndexedSeq(1, 3)
      )
    ),
    (
      vs, Seq(1L, 2L, 4L),
      IndexedSeq(
        IndexedSeq(0, 0, 0),
        IndexedSeq(0, 0, 1),
        IndexedSeq(0, 1, 0),
        IndexedSeq(0, 1, 1),
        IndexedSeq(0, 2, 0),
        IndexedSeq(0, 2, 1),
        IndexedSeq(1, 0, 0),
        IndexedSeq(1, 0, 1),
        IndexedSeq(1, 1, 0),
        IndexedSeq(1, 1, 1),
        IndexedSeq(1, 2, 0),
        IndexedSeq(1, 2, 1)

      )
    )
  )

  "test" should "pass" in {
    forAll(td) { (valueSets, nids, resultValues) =>
      val r = valueSets.valSets(nids)
      r shouldBe resultValues
    }
  }
}
