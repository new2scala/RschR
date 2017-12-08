package org.ditw.graphProb

import org.ditw.graphProb.belUpdating.EnrichedGraphOps
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
    )
  )

  "genJoinTree tests" should "pass" in {
    forAll(genJoinTreeTestData) { (eg, pairs, links) =>
      val (p, l) = eg._genJoinTree
      //val p1 = p.map(p => p._1 -> (p._1 -- p._2))
      p shouldBe pairs
      l shouldBe links
    }
  }
}
