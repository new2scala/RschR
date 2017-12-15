package org.ditw.graphProb

import org.ditw.graphProb.belUpdating.EnrichedGraphOps
import org.ditw.graphProb.belUpdating.Potentials._
import org.ditw.graphProb.belUpdating.ProbModels.ProbModel
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-12-08.
  */
class EnrichedGraphOpsTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val add2CliqueSetTestData = Table(
    ("curr", "nc", "res"),
    (
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3")
      ),
      Set("A1", "A2", "A3"),
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A3")
      ),
      Set("A1", "A2", "A3"),
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3")
      ),
      Set("A1", "A2"),
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4")
      ),
      Set("A1", "A2", "A4"),
      ListBuffer[Set[String]](
        Set("A1", "A2", "A4"),
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4")
      ),
      Set("A2", "A4"),
      ListBuffer[Set[String]](
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A3", "A4"),
        Set("A2", "A3", "A4"),
        Set("A1", "A2", "A3")
      ),
      Set("A2", "A4"),
      ListBuffer[Set[String]](
        Set("A1", "A3", "A4"),
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4")
      )
    ),
    (
      ListBuffer[Set[String]](
        Set("A1", "A3", "A4"),
        Set("A2", "A3", "A4"),
        Set("A1", "A2", "A3")
      ),
      Set("A2", "A5"),
      ListBuffer[Set[String]](
        Set("A1", "A3", "A4"),
        Set("A1", "A2", "A3"),
        Set("A2", "A3", "A4"),
        Set("A2", "A5")
      )
    )
  )

  "add2CliqueSet tests" should "pass" in {
    forAll(add2CliqueSetTestData) { (curr, nc, exp) =>
      val r = EnrichedGraphOps.add2CliqueSet(curr, nc)
      r.toSet shouldBe exp.toSet
    }
  }

  import org.ditw.graphProb.belUpdating.GraphHelpers._
  private val findCliqueTestData = Table(
    ("enrichedGraph", "cliques"),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A2" -> "A3"
      ),
      Set(
        Set("A1", "A2", "A3")
      )
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A2" -> "A3",
        "A2" -> "A4",
        "A3" -> "A4",
        "A4" -> "A5"
      ),
      Set(
        Set("A1", "A2"),
        Set("A2", "A3", "A4"),
        Set("A4", "A5")
      )
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A2" -> "A3",
        "A2" -> "A4",
        "A2" -> "A5",
        "A3" -> "A4",
        "A3" -> "A5",
        "A4" -> "A5",
        "A5" -> "A6"
      ),
      Set(
        Set("A1", "A2"),
        Set("A2", "A3", "A4", "A5"),
        Set("A6", "A5")
      )
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A1" -> "A4",
        "A2" -> "A3",
        "A2" -> "A4",
        "A2" -> "A5",
        "A2" -> "A6",
        "A3" -> "A4",
        "A3" -> "A5",
        "A3" -> "A6",
        "A4" -> "A5",
        "A4" -> "A6",
        "A5" -> "A6"
      ),
      Set(
        Set("A1", "A2", "A3", "A4"),
        Set("A2", "A3", "A4", "A5", "A6")
      )
    ),
    (
      buildGraph(
        "A" -> "B",
        "A" -> "C",
        "A" -> "D",
        "B" -> "C",
        "B" -> "D",
        "B" -> "E",
        "B" -> "G",
        "C" -> "D",
        "C" -> "E",
        "C" -> "G",
        "C" -> "H",
        "C" -> "J",
        "D" -> "E",
        "D" -> "F",
        "D" -> "G",
        "D" -> "I",
        "E" -> "F",
        "E" -> "I",
        "F" -> "I",
        "G" -> "H",
        "G" -> "J",
        "H" -> "J"
      ),
      Set(
        Set("A", "B", "C", "D"),
        Set("B", "C", "D", "E"),
        Set("B", "C", "D", "G"),
        Set("D", "E", "F", "I"),
        Set("C", "G", "H", "J")
      )
    )
  )

  import EnrichedGraphOps._
  "findClique tests" should "pass" in {
    forAll(findCliqueTestData) { (eg, cl) =>
      val c = eg.findCliques
      c shouldBe cl
    }
  }

  private val genJoinTreeTestData = Table(
    ("enrichedGraph", "nodeSetPairs", "links"),
    (
      buildGraph(
        "A" -> "B",
        "A" -> "C",
        "A" -> "D",
        "B" -> "C",
        "B" -> "D",
        "B" -> "E",
        "B" -> "G",
        "C" -> "D",
        "C" -> "E",
        "C" -> "G",
        "C" -> "H",
        "C" -> "J",
        "D" -> "E",
        "D" -> "F",
        "D" -> "G",
        "D" -> "I",
        "E" -> "F",
        "E" -> "I",
        "F" -> "I",
        "G" -> "H",
        "G" -> "J",
        "H" -> "J"
      ),
      IndexedSeq(
        Set("A", "B", "C", "D") -> Set("B", "C", "D"),
        Set("D", "E", "F", "I") -> Set("D", "E"),
        Set("B", "C", "D", "E") -> Set("B", "C", "D"),
        Set("B", "C", "D", "G") -> Set("C", "G"),
        Set("C", "G", "H", "J") -> Set()
      ),
      List(
        0 -> 3,
        1 -> 2,
        2 -> 3,
        3 -> 4
      )
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A2" -> "A3",
        "A2" -> "A4",
        "A2" -> "A5",
        "A3" -> "A5",
        "A3" -> "A6"
      ),
      IndexedSeq(
        Set("A1", "A2", "A3") -> Set("A2", "A3"),
        Set("A2", "A4") -> Set("A2"),
        Set("A2", "A3", "A5") -> Set("A3"),
        Set("A3", "A6") -> Set()
      ),
      List(
        0 -> 2,
        1 -> 2,
        2 -> 3
      )
    )
  )

  import collection.JavaConverters._

  "genJoinTree tests" should "pass" in {
    forAll(genJoinTreeTestData) { (eg, pairs, links) =>
      val (p, l) = eg._genJoinTree
      //val p1 = p.map(p => p._1 -> (p._1 -- p._2))
      p.indices.foreach { idx =>
        val pp = p(idx)
        val ep = pairs(idx)
        pp._1.data shouldBe ep._1
        pp._2.data shouldBe ep._2
      }
      l shouldBe links

      val jt = eg.joinTree
      //println(jt)
      jt.allEdges.foreach { edge =>
        val ed = jt.edgeData(edge._1, edge._2)
        ed shouldNot be(null)
      }
    }
  }

  private val junctionTreeTestData = Table(
    ("model", "enrichedGraph", "nodeSetPairs", "links"),
    (
      ProbModel(
        List(
          Potential(Set("F1"), Set(), dummy),
          Potential(Set("F1"), Set("F2"), dummy),
          Potential(Set("F1"), Set("F3"), dummy),
          Potential(Set("F2"), Set("F4"), dummy),
          Potential(Set("F5"), Set("F2", "F3"), dummy),
          Potential(Set("F3"), Set("F6"), dummy)
        )
      ),
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A2" -> "A3",
        "A2" -> "A4",
        "A2" -> "A5",
        "A3" -> "A5",
        "A3" -> "A6"
      ),
      IndexedSeq(
        Set("A1", "A2", "A3") -> Set("A2", "A3"),
        Set("A2", "A4") -> Set("A2"),
        Set("A2", "A3", "A5") -> Set("A3"),
        Set("A3", "A6") -> Set()
      ),
      List(
        0 -> 2,
        1 -> 2,
        2 -> 3
      )
    ),
    (
      // example from "variable-elimination.pdf"
      ProbModel(
        List(
          Potential(Set("A"), Set(), dummy),
          Potential(Set("B"), Set("A"), dummy),
          Potential(Set("C"), Set("A"), dummy),
          Potential(Set("D"), Set("B", "C"), dummy),
          Potential(Set("E"), Set("C"), dummy)
        )
      ),
      buildGraph(
        "A" -> "B",
        "A" -> "C",
        "B" -> "C",
        "B" -> "D",
        "C" -> "D",
        "C" -> "E"
      ),
      IndexedSeq(
        Set("A", "B", "C") -> Set("B", "C"),
        Set("B", "C", "D") -> Set("C"),
        Set("C", "E") -> Set()
      ),
      List(
        0 -> 1,
        1 -> 2
      )
    )
  )


  "junctionTreeTest tests" should "pass" in {
    forAll(junctionTreeTestData) { (model, eg, pairs, links) =>
      val jut = buildJunctionTree(model)

      jut.allNodes.foreach { n =>
        val leaves = jut.leafNodesFrom(n)
        println(leaves)
      }

    }
  }

  private val junctionTreeWithPotentialTestData = Table(
    ("model", "enrichedGraph", "nodeSetPairs", "links"),
    (
      // example from "variable-elimination.pdf"
      ProbModel(
        List(
          Potential(
            Set("A"), Set(),
            PotentialData(BooleanVars1, Array(0.6, 0.4))
          ),
          Potential(
            Set("B"), Set("A"),
            PotentialData(BooleanVars2, Array(0.2, 0.8, 0.75, 0.25))
          ),
          Potential(
            Set("C"), Set("A"),
            PotentialData(BooleanVars2, Array(0.8, 0.2, 0.1, 0.9))
          ),
          Potential(
            Set("D"), Set("B", "C"),
            PotentialData(BooleanVars3, Array(0.95, 0.05, 0.9, 0.1, 0.8, 0.2, 0.0, 1.0))
          ),
          Potential(
            Set("E"), Set("C"),
            PotentialData(BooleanVars2, Array(0.7, 0.3, 0.0, 1.0))
          )
        )
      ),
      buildGraph(
        "A" -> "B",
        "A" -> "C",
        "B" -> "C",
        "B" -> "D",
        "C" -> "D",
        "C" -> "E"
      ),
      IndexedSeq(
        Set("A", "B", "C") -> Set("B", "C"),
        Set("B", "C", "D") -> Set("C"),
        Set("C", "E") -> Set()
      ),
      List(
        0 -> 1,
        1 -> 2
      )
    )
  )

  "junctionTreeWithPotentialTest tests" should "pass" in {
    forAll(junctionTreeWithPotentialTestData) { (model, eg, pairs, links) =>
      val jut = buildJunctionTree(model)

      println(jut)

    }
  }
}
