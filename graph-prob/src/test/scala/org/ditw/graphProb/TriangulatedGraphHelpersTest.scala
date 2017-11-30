package org.ditw.graphProb

import org.ditw.graphProb.JGraphSmokeTest.dummy
import org.ditw.graphProb.belUpdating.GraphHelpers.{Potential, ProbModel}
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
          Potential(Set("F1", "F2"), dummy),
          Potential(Set("F1", "F3"), dummy),
          Potential(Set("F2", "F4"), dummy),
          Potential(Set("F2", "F3", "F5"), dummy),
          Potential(Set("F3", "F6"), dummy)
        )
      ),
      Set("F1", "F4", "F5", "F6")
    ),
    (
      ProbModel(
        List(
          Potential(Set("A1", "A2", "A3", "A4"), dummy),
          Potential(Set("A5", "A3"), dummy),
          Potential(Set("A2", "A5"), dummy),
          Potential(Set("A4", "A5"), dummy)
        )
      ),
      Set("A1", "A5")
    ),
    (
      ProbModel(
        List(
          Potential(Set("A1", "A2", "A3"), dummy),
          Potential(Set("A1", "A3", "A4"), dummy),
          Potential(Set("A2", "A3", "A5"), dummy),
          Potential(Set("A3", "A4", "A5"), dummy)
        )
      ),
      Set()
    )
  )

  import org.ditw.graphProb.belUpdating.GraphHelpers._
  import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers._
  "test" should "pass" in {
    forAll(testData) { (model, snodes) =>
      val g = graphFromModel[VertexEdge](model)
      val found = findSimplicialNodes(g)
      found shouldBe snodes
    }
  }
}
