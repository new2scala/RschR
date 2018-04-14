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
  //    0  1    0.65
  //    1  1    0.35
  val pot31 = Potential(
    3L, IndexedSeq(1L), vs1,
    IndexedSeq(0.32, 0.68, 0.65, 0.35)
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

  val p1 = ProbDistr(IndexedSeq(1L), vs1, IndexedSeq(0.45, 0.55))

  val pot63e6 = pot63.eliminate(Set(6L))
  assert(pot63e6 == ProbDistr(IndexedSeq(3L), vs1, IndexedSeq(1.0, 1.0)))
  println("φ6': eliminating A6 from φ6 - P(A6|A3) ... checked")

  val pot523e5 = pot523.eliminate(Set(5L))
  assert(pot523e5 == ProbDistr(IndexedSeq(2L, 3L), vs1, IndexedSeq(1.0, 1.0, 1.0, 1.0)))
  println("φ5': eliminating A5 from φ5 - P(A5|A2,A3) ... checked")

  val pot523_e5_63_e6 = pot523e5.mul(pot63e6)
  assert(pot523_e5_63_e6 == ProbDistr(IndexedSeq(2L, 3L), vs1, IndexedSeq(1.0, 1.0, 1.0, 1.0)))
  println("φ6' x φ5': ... checked")

  val pd_pre_e653 = pot31.mul(pot523e5.mul(pot63e6))
  val pd_e653 = pd_pre_e653.eliminate(Set(3L))
  assert(pd_e653 == ProbDistr(IndexedSeq(1L, 2L), vs1, IndexedSeq(1.0, 1.0, 1.0, 1.0)))
  println("φ3' = φ6' x φ5' x φ3: ... checked")
  // all 1's so far, because A3, A5 and A6 are so-called barren nodes BDNG page 112:
  //   a node A is barren if neither A or any of A's descendants have received evidence.
  //   The conditional prob attached to a barren node has an impact only on descendant nodes.
  //   (not anscestral nodes, or nodes outside of the sub-graph)

  val pd_pre_e6532 = pot21.mul(pot42.prob).mul(pd_e653)
  val pd_e6532 = pd_pre_e6532.eliminate(Set(2L))
  assert(
    pd_e6532 == ProbDistr(IndexedSeq(4L, 1L), vs1,
      IndexedSeq(
        0.28*0.16 + 0.36*0.84,
        0.72*0.16 + 0.64*0.84,
        0.28*0.24 + 0.36*0.76,
        0.72*0.24 + 0.64*0.76
      )
    )
  )
  println("φ2' = φ6' x φ5' x φ3': ... checked")

  val pd_pre_e65321 = pd_e6532.mul(p1)
  val pd_e65321 = pd_pre_e65321.eliminate(Set(1L))
  assert(
    pd_e65321 == ProbDistr(IndexedSeq(4L), vs1,
      IndexedSeq(
        (0.28*0.16 + 0.36*0.84)*0.45 + (0.28*0.24 + 0.36*0.76)*0.55,
        (0.72*0.16 + 0.64*0.84)*0.45 + (0.72*0.24 + 0.64*0.76)*0.55
      ) // (0.34368, 0.65632)
    )
  )
  println("p4 = φ2' x p1: ... checked")
  println(s"\t${pd_e65321.probs}")

  //val pot523_e5_63_e6 = pot523.
  println("===========================================")
  println("Putting all together ...")
  val pd_all =
    pot31.mul(
      pot63
        .eliminate(Set(6L))
        .mul(pot523.eliminate(Set(5L)))
    )
    .eliminate(Set(3L))
    .mul(
      pot21.mul(pot42.prob)
    )
    .eliminate(Set(2L))
    .mul(p1)
    .eliminate(Set(1L))
  val P4_0 = ProbDistr(IndexedSeq(4L), vs1,
    IndexedSeq(0.34368, 0.65632) // (0.34368, 0.65632)
  )
  assert(pd_all == P4_0)
  println("p4 = [all ops] ... checked")


  //// tests with evidence
  // with evidence A6=0
  val pd_evd60 = pot63.applyEvidence(0)
  val pd_evd61 = pot63.applyEvidence(1)
  assert(
    pd_evd60 == ProbDistr(IndexedSeq(3L), vs1,
      IndexedSeq(0.12, 0.20)
    )
  )
  assert(
    pd_evd61 == ProbDistr(IndexedSeq(3L), vs1,
      IndexedSeq(0.88, 0.80)
    )
  )
  println("apply evidence A6=0 ... checked")

  println("Apply evidence A6=0 in calculating P(A4) ...")
  val pd_all_evd60 =
    pot31.mul(
      pd_evd60.mul(pot523.eliminate(Set(5L)))
    )
    .eliminate(Set(3L))
    .mul(
      pot21.mul(pot42.prob)
    )
    .eliminate(Set(2L))
    .mul(p1)
    .eliminate(Set(1L))
  println(s"\t${pd_all_evd60.traceProbsNorm}")

  val pd_all_evd61 =
    pot31.mul(
      pd_evd61.mul(pot523.eliminate(Set(5L)))
    )
    .eliminate(Set(3L))
    .mul(
      pot21.mul(pot42.prob)
    )
    .eliminate(Set(2L))
    .mul(p1)
    .eliminate(Set(1L))
  println(s"\t${pd_all_evd61.traceProbsNorm}")

  //// tests with evidence
  // with evidence A5=0
  val pd_evd50 = pot523.applyEvidence(0)
  val pd_evd51 = pot523.applyEvidence(1)
  assert(
    pd_evd50 == ProbDistr(IndexedSeq(2L, 3L), vs1,
      IndexedSeq(0.1, 0.3, 0.15, 0.25)
    )
  )
  assert(
    pd_evd51 == ProbDistr(IndexedSeq(2L, 3L), vs1,
      IndexedSeq(0.9, 0.7, 0.85, 0.75)
    )
  )
  println("apply evidence A5=0 ... checked")
  println("Apply evidence A5=0 in calculating P(A4) ...")
  val pd_all_evd50 =
    pot31.mul(
      pot63.eliminate(Set(6L)).mul(pd_evd50)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd50.traceProbsNorm}")

  val pd_all_evd51 =
    pot31.mul(
      pot63.eliminate(Set(6L)).mul(pd_evd51)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd51.traceProbsNorm}")

  println("Apply evidence A5=0 & A6=0 in calculating P(A4) ...")
  val pd_all_evd5060 =
    pot31.mul(
      pd_evd60.mul(pd_evd50)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd5060.traceProbs}")
  val pd_all_evd5160 =
    pot31.mul(
      pd_evd60.mul(pd_evd51)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd5160.traceProbs}")
  val pd_all_evd5161 =
    pot31.mul(
      pd_evd61.mul(pd_evd51)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd5161.traceProbs}")
  val pd_all_evd5061 =
    pot31.mul(
      pd_evd61.mul(pd_evd50)
    )
      .eliminate(Set(3L))
      .mul(
        pot21.mul(pot42.prob)
      )
      .eliminate(Set(2L))
      .mul(p1)
      .eliminate(Set(1L))
  println(s"\t${pd_all_evd5061.traceProbs}")

  //  BNDG page 126:
  //   V6: A2, A4     --> S4: A2 ---\
  //   V2: A2, A3, A5 --> S2: A2, A3 -> V4: A1, A2, A3
  //   V1: A3, A6     --> S1: A3 ---/
  // collecting evidence to V6 for A4
  def collectEvd2A4(pV1:ProbDistr, pV2:ProbDistr):ProbDistr = {
    //   V6 <- ψ1 = φ6.elim(A6)
    val psi1 = pV1 //p63.eliminate(Set(6L))
    //   V6 <- ψ2 = φ5.elim(A5)
    val psi2 = pV2 //p523.eliminate(Set(5L))
    //   V6 <- ψ^4 = Φ4.elim(A1,A3), where Φ4 = ψ1,ψ2,φ1,φ2,φ3
    val _psi4u = psi1.mul(psi2).mul(pot31.prob).eliminate(Set(3L))
    println(s"\t${_psi4u.traceProbs}")
    val psi4u = _psi4u.mul(pot21.prob).mul(p1).eliminate(Set(1L))
    val _p4 = pot42.mul(psi4u).eliminate(Set(2L))
    println(s"\t${_p4.traceProbs}")
    _p4
  }

  println("collecting evidence to V6 for A4:")
  val p4a = collectEvd2A4(
    pot63.eliminate(Set(6L)),
    pot523.eliminate(Set(5L))
  )
  assert(p4a == P4_0)
  println("With evidence A6 == 0")
  val p4b = collectEvd2A4(
    pot63.applyEvidence(0),
    pot523.eliminate(Set(5L))
  )
  println("With evidence A6 == 1")
  val p4c = collectEvd2A4(
    pot63.applyEvidence(1),
    pot523.eliminate(Set(5L))
  )
  println("With evidence A5 == 0")
  val p4d = collectEvd2A4(
    pot63.eliminate(Set(6L)),
    pot523.applyEvidence(0)
  )
  println("With evidence A5 == 1")
  val p4e = collectEvd2A4(
    pot63.eliminate(Set(6L)),
    pot523.applyEvidence(1)
  )
  //assert(p4a == P4_0)
}
