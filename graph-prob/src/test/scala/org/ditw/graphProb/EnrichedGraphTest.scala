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

  def _buildGraph(edges:Seq[(String,String)]):EnrichedGraph[VertexEdge] = {
    val vtxs = edges.flatMap(p => Set(p._1, p._2))
    val g = new SimpleGraph[String, VertexEdge](classOf[VertexEdge])
    vtxs.foreach(g.addVertex)
    edges.foreach(p => g.addEdge(p._1, p._2))
    val r = new EnrichedGraph[VertexEdge](g)
    r
  }
  def buildGraph(edges:(String,String)*):EnrichedGraph[VertexEdge] = {
    _buildGraph(edges)
  }

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
        "A1" -> "A2",
        "A2" -> "A3"
      ),
      Option(
        buildGraph(
          "A2" -> "A3"
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
        val r = res.get

        graphsEqual(e, r) shouldBe true
      }

    }
  }

}
