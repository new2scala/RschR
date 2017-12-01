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

  def buildGraph(edges:(String,String)*):EnrichedGraph[VertexEdge] = {
    val vtxs = edges.flatMap(p => Set(p._1, p._2))
    val g = new SimpleGraph[String, VertexEdge](classOf[VertexEdge])
    vtxs.foreach(g.addVertex)
    edges.foreach(p => g.addEdge(p._1, p._2))
    val r = new EnrichedGraph[VertexEdge](g)
    r
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

}
