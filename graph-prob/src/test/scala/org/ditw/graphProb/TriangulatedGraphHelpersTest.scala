package org.ditw.graphProb

import org.ditw.graphProb.belUpdating.EnrichedGraph
import org.ditw.graphProb.belUpdating.Potentials._
import org.ditw.graphProb.belUpdating.ProbModels.ProbModel
import org.jgrapht.graph.SimpleGraph
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2017-11-30.
  */
class TriangulatedGraphHelpersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val testData = Table(
    ("model", "simplicialNodeSet"),
    (
      ProbModel(
        List(
          Potential(Set("F1", "F2"), Set(), dummy),
          Potential(Set("F1", "F3"), Set(), dummy),
          Potential(Set("F2", "F4"), Set(), dummy),
          Potential(Set("F2", "F3", "F5"), Set(), dummy),
          Potential(Set("F3", "F6"), Set(), dummy)
        )
      ),
      Set("F1", "F4", "F5", "F6")
    ),
    (
      ProbModel(
        List(
          Potential(Set("A1", "A2", "A3", "A4"), Set(), dummy),
          Potential(Set("A5", "A3"), Set(), dummy),
          Potential(Set("A2", "A5"), Set(), dummy),
          Potential(Set("A4", "A5"), Set(), dummy)
        )
      ),
      Set("A1", "A5")
    ),
    (
      ProbModel(
        List(
          Potential(Set("A1", "A2", "A3"), Set(), dummy),
          Potential(Set("A1", "A3", "A4"), Set(), dummy),
          Potential(Set("A2", "A3", "A5"), Set(), dummy),
          Potential(Set("A3", "A4", "A5"), Set(), dummy)
        )
      ),
      Set()
    )
  )

  import org.ditw.graphProb.belUpdating.GraphHelpers._
  import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers._
  "findSimplicialNodes tests" should "pass" in {
    forAll(testData) { (model, snodes) =>
      val g:SimpleGraph[String, VertexEdge] = graphFromModel(model)
      val eg = new EnrichedGraph(g)
      val found = eg.simplicialNodes
      found shouldBe snodes
    }
  }
}
