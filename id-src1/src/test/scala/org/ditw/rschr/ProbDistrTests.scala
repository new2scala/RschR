package org.ditw.rschr

import org.ditw.rschr.Bayne.{NodeId, NodeValueSets, ProbDistr}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-02-23.
  */
class ProbDistrTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

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

//  private def genTestProbDistrs(vs:NodeValueSets, condVarNodeId:NodeId):ProbDistr = {
//
//  }

  "test" should "pass" in {
    forAll(td) { (valueSets, nids, resultValues) =>
      val r = valueSets.valIndicesOf(nids)
      r shouldBe resultValues
    }
  }

  private val td_reverse = Table(
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
        IndexedSeq(1, 0),
        IndexedSeq(0, 1),
        IndexedSeq(1, 1),
        IndexedSeq(0, 2),
        IndexedSeq(1, 2)
      )
    ),
    (
      vs, Seq(1L, 2L, 4L),
      IndexedSeq(
        IndexedSeq(0, 0, 0),
        IndexedSeq(1, 0, 0),
        IndexedSeq(0, 1, 0),
        IndexedSeq(1, 1, 0),
        IndexedSeq(0, 2, 0),
        IndexedSeq(1, 2, 0),
        IndexedSeq(0, 0, 1),
        IndexedSeq(1, 0, 1),
        IndexedSeq(0, 1, 1),
        IndexedSeq(1, 1, 1),
        IndexedSeq(0, 2, 1),
        IndexedSeq(1, 2, 1)

      )
    )
  )

    "reverse test" should "pass" in {
    forAll(td_reverse) { (valueSets, nids, resultValues) =>
      val r = valueSets.valReverseIndicesOf(nids)
      r shouldBe resultValues
    }
  }

  private val vs0 = NodeValueSets(
    Map(1L -> 2, 2L -> 2, 3L -> 2, 4L -> 2)
  )
  ////
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
  //
  private val distr0 = ProbDistr(
    1L to 4L, vs0,
    IndexedSeq(
      0.12, 0.88, 0.20, 0.80, 0.28, 0.72, 0.36, 0.64,
      0.16, 0.84, 0.24, 0.76, 0.32, 0.68, 0.40, 0.60
    )
  )

  import TestUtils._

  "prob test" should "pass" in {
    var p01 = distr0.prob(IndexedSeq(0, 0, 0, 1))
    doubleEquals(p01, 0.16) shouldBe true

    p01 = distr0.prob(IndexedSeq(0, 0, 1, 0))
    doubleEquals(p01, 0.28) shouldBe true
    p01 = distr0.prob(IndexedSeq(1, 0, 1, 0))
    doubleEquals(p01, 0.72) shouldBe true

    p01 = distr0.prob(IndexedSeq(0, 1, 1, 1))
    doubleEquals(p01, 0.40) shouldBe true
  }

  private val distr40 = ProbDistr(
    1L to 3L, vs0,
    IndexedSeq(
      0.12, 0.88, 0.20, 0.80, 0.28, 0.72, 0.36, 0.64
    )
  )

  private val distr41 = ProbDistr(
    1L to 3L, vs0,
    IndexedSeq(
      0.16, 0.84, 0.24, 0.76, 0.32, 0.68, 0.40, 0.60
    )
  )

  private val vs1 = NodeValueSets(
    Map(1L -> 2, 2L -> 3, 3L -> 2)
  )
  ////
  //   A  B  C   P(A|B,C)
  //   0  0  0     0.12
  //   1  0  0     0.88
  //   0  1  0     0.20
  //   1  1  0     0.80
  //   0  2  0     0.28
  //   1  2  0     0.72
  //   0  0  1     0.36
  //   1  0  1     0.64
  //   0  1  1     0.16
  //   1  1  1     0.84
  //   0  2  1     0.24
  //   1  2  1     0.76
  //
  private val distr1 = ProbDistr(
    1L to 3L, vs1,
    IndexedSeq(
      0.12, 0.88, 0.20, 0.80, 0.28, 0.72,
      0.36, 0.64, 0.16, 0.84, 0.24, 0.76
    )
  )


  private val probDistrTestData1 = Table(
    ("distrIn", "ofNodes", "distrsOut"),
    (
      distr1,
      Seq(3L, 1L),
      IndexedSeq(
        ProbDistr(
          IndexedSeq(2L), vs1,
          IndexedSeq(
            0.12, 0.20, 0.28
          )
        ),
        ProbDistr(
          IndexedSeq(2L), vs1,
          IndexedSeq(
            0.36, 0.16, 0.24
          )
        ),
        ProbDistr(
          IndexedSeq(2L), vs1,
          IndexedSeq(
            0.88, 0.80, 0.72
          )
        ),
        ProbDistr(
          IndexedSeq(2L), vs1,
          IndexedSeq(
            0.64, 0.84, 0.76
          )
        )
      )
    ),
    (
      distr1,
      Seq(2L, 3L),
      IndexedSeq(
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.12, 0.88
          )
        ),
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.20, 0.80
          )
        ),
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.28, 0.72
          )
        ),
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.36, 0.64
          )
        ),
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.16, 0.84
          )
        ),
        ProbDistr(
          IndexedSeq(1L), vs1,
          IndexedSeq(
            0.24, 0.76
          )
        )
      )
    ),
    (
      distr1,
      Seq(3L),
      IndexedSeq(
        ProbDistr(
          IndexedSeq(1L, 2L), vs1,
          IndexedSeq(
            0.12, 0.88, 0.20, 0.80, 0.28, 0.72
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 2L), vs1,
          IndexedSeq(
            0.36, 0.64, 0.16, 0.84, 0.24, 0.76
          )
        )
      )
    ),
    (
      distr0,
      Seq(4L),
      IndexedSeq(
        distr40,
        distr41
      )
    ),
    (
      distr0,
      Seq(2L, 4L),
      IndexedSeq(
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.12, 0.88, 0.28, 0.72
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.20, 0.80, 0.36, 0.64
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.16, 0.84, 0.32, 0.68
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.24, 0.76, 0.40, 0.60
          )
        )
      )
    ),
    (
      distr0,
      Seq(4L, 2L),
      IndexedSeq(
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.12, 0.88, 0.28, 0.72
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.16, 0.84, 0.32, 0.68
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.20, 0.80, 0.36, 0.64
          )
        ),
        ProbDistr(
          IndexedSeq(1L, 3L), vs0,
          IndexedSeq(
            0.24, 0.76, 0.40, 0.60
          )
        )
      )
    ),
    (
      distr0,
      Seq(3L, 4L),
      IndexedSeq(
        ProbDistr(
          1L to 2L, vs0,
          IndexedSeq(
            0.12, 0.88, 0.20, 0.80
          )
        ),
        ProbDistr(
          1L to 2L, vs0,
          IndexedSeq(
            0.28, 0.72, 0.36, 0.64
          )
        ),
        ProbDistr(
          1L to 2L, vs0,
          IndexedSeq(
            0.16, 0.84, 0.24, 0.76
          )
        ),
        ProbDistr(
          1L to 2L, vs0,
          IndexedSeq(
            0.32, 0.68, 0.40, 0.60
          )
        )
      )
    )
  )

  "probsOf test" should "pass" in {
    forAll(probDistrTestData1) { (distrIn, ofNodes, distrsOut) =>
      val outDistr = distrIn.probsOf(ofNodes)
      outDistr shouldBe distrsOut
    }
  }

  private val mulTestData = Table(
    ("pd1", "pd2", "resultPd"),
    (
      ProbDistr(
        1L to 2L, vs0,
        IndexedSeq(
          0.12, 0.88, 0.20, 0.80
        )
      ),
      ProbDistr(
        IndexedSeq(3L), vs0,
        IndexedSeq(
          0.1, 0.9
        )
      ),
      ProbDistr(
        1L to 3L, vs0,
        IndexedSeq(
          0.012, 0.088, 0.020, 0.080,
          0.108, 0.792, 0.18, 0.72
        )
      )
    ),
    (
      ProbDistr(
        1L to 2L, vs0,
        IndexedSeq(
          0.12, 0.88, 0.20, 0.80
        )
      ),
      ProbDistr(
        IndexedSeq(3L, 4L), vs0,
        IndexedSeq(
          0.1, 0.9, 0.4, 0.6
        )
      ),
      ProbDistr(
        1L to 4L, vs0,
        IndexedSeq(
          0.012, 0.088, 0.020, 0.080,
          0.108, 0.792, 0.18, 0.72,
          0.048, 0.352, 0.08, 0.32,
          0.072, 0.528, 0.12, 0.48
        )
      )
    )
  )

  "mul tests" should "pass" in {
    forAll(mulTestData) { (pd1, pd2, resultPd) =>
      val npd = pd1.mul_nc(pd2)
      npd shouldBe resultPd
    }
  }
}
