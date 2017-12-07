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
}
