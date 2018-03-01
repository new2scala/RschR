package org.ditw.rschr.utils

import org.ditw.rschr.Bayne.{NodeValueSets, Potential, ProbDistr}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-02-28.
  */
object BNTestFromBNDG extends App {

  ////
  //            A1
  //           /  \
  //         A2   A3
  //        /  \ /  \
  //      A4   A5   A6
  //   direction: up -> down



  val vs1 = NodeValueSets(
    Map(
      1L -> 2,
      2L -> 2,
      3L -> 2,
      4L -> 2,
      5L -> 2,
      6L -> 2
    )
  )

  //// Phi_63
  //   A6 A3  P(A6|A3)
  //    0  0    0.12
  //    1  0    0.88
  //    0  1    0.20
  //    1  1    0.80
  val pot63 = Potential(
    6L, IndexedSeq(3L), vs1,
    IndexedSeq(0.12, 0.88, 0.2, 0.8)
  )
  //// Phi_42
  //   A4 A2  P(A4|A2)
  //    0  0    0.28
  //    1  0    0.72
  //    0  1    0.36
  //    1  1    0.64
  val pot42 = Potential(
    4L, IndexedSeq(2L), vs1,
    IndexedSeq(0.28, 0.72, 0.36, 0.64)
  )
  //// Phi_21
  //   A2 A1  P(A2|A1)
  //    0  0    0.16
  //    1  0    0.84
  //    0  1    0.24
  //    1  1    0.76
  val pot21 = Potential(
    2L, IndexedSeq(1L), vs1,
    IndexedSeq(0.16, 0.84, 0.24, 0.76)
  )
  //// Phi_31
  //   A3 A1  P(A3|A1)
  //    0  0    0.32
  //    1  0    0.68
  //    0  1    0.40
  //    1  1    0.40
  val pot31 = Potential(
    3L, IndexedSeq(1L), vs1,
    IndexedSeq(0.32, 0.68, 0.40, 0.40)
  )
  //// Phi_523
  //   A5 A2 A3  P(A5|A2,A3)
  //   0  0  0      0.1
  //   1  0  0      0.9
  //   0  1  0      0.3
  //   1  1  0      0.7
  //   0  0  1      0.15
  //   1  0  1      0.85
  //   0  1  1      0.25
  //   1  1  1      0.75

  val pot523 = Potential(
    5L, IndexedSeq(2L, 3L), vs1,
    IndexedSeq(
      0.1, 0.9,
      0.3, 0.7,
      0.15, 0.85,
      0.25, 0.75
    )
  )

  val pot63e6 = pot63.eliminate(Set(6L))
  assert(pot63e6 == ProbDistr(IndexedSeq(3L), vs1, IndexedSeq(1.0, 1.0)))
  println("φ6': eliminating A6 from φ6 - P(A6|A3) ... checked")

  val pot523e5 = pot523.eliminate(Set(5L))
  assert(pot523e5 == ProbDistr(IndexedSeq(2L, 3L), vs1, IndexedSeq(1.0, 1.0, 1.0, 1.0)))
  println("φ5': eliminating A5 from φ5 - P(A5|A2,A3) ... checked")

  val pot523_e5_63_e6 = pot523e5.mul(pot63e6)
  assert(pot523_e5_63_e6 == ProbDistr(IndexedSeq(2L, 3L), vs1, IndexedSeq(1.0, 1.0, 1.0, 1.0)))
  println("φ6' x φ5': ... checked")

  //val pot523_e5_63_e6 = pot523.
}
