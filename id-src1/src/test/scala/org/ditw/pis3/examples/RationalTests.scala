package org.ditw.pis3.examples

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class RationalTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  val gcdTestData = Table(
    ("n1", "n2", "gcd"),
    (-10, -18, 2),
    (10, -18, 2),
    (0, -3, 1),
    (0, 3, 1),
    (1, 3, 1),
    (10, 3, 1),
    (2048, 2048, 2048),
    (10, 8, 2),
    (12, 8, 4),
    (8, 10, 2),
    (8, 12, 4)
  )

  "gcd tests" should "pass" in {
    forAll (gcdTestData) { (n1, n2, result) =>
      val res = Rational.gcd(n1, n2)
      res shouldBe result
    }
  }

  val plusTestData = Table(
    ("n1", "d1", "n2", "d2", "resn", "resd"),
    (1, 2, 2, 3, 7, 6),
    (1, 2, 1, 2, 1, 1)
  )

  "+ tests" should "pass" in {
    forAll (plusTestData) { (n1, d1, n2, d2, resn, resd) =>
      val r1 = new Rational(n1, d1)
      val r2 = new Rational(n2, d2)

      val resExp = new Rational(resn, resd)

      val res = r1 + r2
      resExp shouldBe res
    }
  }

  val intContractorTestData = Table(
    ("n1"),
    (1),
    (0),
    (10),
    (-1)
  )

  "intContractor tests" should "pass" in {
    forAll (intContractorTestData) { n1 =>
      val r1 = new Rational(n1)

      r1 shouldBe new Rational(n1, 1)
    }
  }

  val equalsTestData = Table(
    ("n1", "d1", "n2", "d2", "equals"),
    (0, 2, 0, 4, true),
    (0, 2, 0, -4, true),
    (0, 2, 0, -2, true),
    (1, 2, 2, 3, false),
    (1, 2, 2, 4, true),
    (1, 2, -2, -4, true),
    (1, -2, 2, -4, true),
    (-1, 2, 2, -4, true),
    (-1, -2, 2, -4, false),
    (10, 200, 5, 100, true)
  )
  "equals tests" should "pass" in {
    forAll(equalsTestData) { (n1, d1, n2, d2, res) =>
      val r1 = new Rational(n1, d1)
      val r2 = new Rational(n2, d2)

      val equals = r1 == r2
      equals shouldBe res
    }
  }

}
