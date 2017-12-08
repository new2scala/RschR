package org.ditw.graphProb

import org.ditw.graphProb.belUpdating.EnrichedGraph
import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgrapht.Graph
import org.jgrapht.graph.SimpleGraph
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2017-12-01.
  */
class EnrichedGraphTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import org.ditw.graphProb.belUpdating.GraphHelpers._

  private val familyTestData = Table(
    ("g", "vtx", "nbr"),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A3" -> "A4"
      ),
      "A1",
      Set("A2", "A3")
    )
  )

  "neighbor/family test" should "pass" in {
    forAll(familyTestData) { (g, vtx, nbr) =>
      val neighbors = g.neighbors(vtx)
      neighbors shouldBe nbr
      val family = neighbors+vtx
      family shouldBe g.family(vtx)
    }
  }

  private def buildGraphPair(edges:(String,String)*):(EnrichedGraph[VertexEdge], EnrichedGraph[VertexEdge]) = {
    val g:EnrichedGraph[VertexEdge] = _buildGraph(edges)
    (g, g)
  }

  private val cloneTestData = Table(
    ("g", "cl"),
    buildGraphPair(
      "A1" -> "A2",
      "A1" -> "A3",
      "A3" -> "A4"
    )
  )

  private def graphsEqual[E <: VertexEdge](g1:EnrichedGraph[E], g2:EnrichedGraph[E]):Boolean = {
    g1.vertices == g2.vertices &&
    g1.edges.map(_.toString) == g2.edges.map(_.toString)
  }
  "clone test" should "pass" in {
    forAll(cloneTestData) { (g, cl) =>
      val c = g.cloneGraph

      graphsEqual(c, cl) shouldBe true
    }
  }

  private val eliminateOneTestData = Table(
    ("g", "exp"),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A2" -> "A3"
      ),
      Option(
        buildGraph(
          "A2" -> "A3"
        )
      )
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A1" -> "A4",
        "A2" -> "A3",
        "A2" -> "A5",
        "A3" -> "A4",
        "A3" -> "A5",
        "A4" -> "A5"
      ),
      None
    ),
    (
      buildGraph(
        "A1" -> "A2",
        "A1" -> "A3",
        "A1" -> "A4",
        "A2" -> "A3",
        "A2" -> "A4", // connect A2 - A4 to make it triangulated
        "A2" -> "A5",
        "A3" -> "A4",
        "A3" -> "A5",
        "A4" -> "A5"
      ),
      Option(
        buildGraph(
          "A2" -> "A3",
          "A2" -> "A4",
          "A2" -> "A5",
          "A3" -> "A4",
          "A3" -> "A5",
          "A4" -> "A5"
        )
      )
    )
  )

  "eliminateOne test" should "pass" in {
    forAll(eliminateOneTestData) { (g, exp) =>
      val res = g._eliminateOne

      if (exp.nonEmpty) {
        res.nonEmpty shouldBe true
        val e = exp.get
        val r = res.get._2

        graphsEqual(e, r) shouldBe true
      }
      else {
        res.nonEmpty shouldBe false
      }

    }
  }

}
