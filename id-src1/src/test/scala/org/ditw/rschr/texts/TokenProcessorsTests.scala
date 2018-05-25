package org.ditw.rschr.texts

import org.ditw.texts.TokenProcessors.{SpaceSepTkzr, Tkzr}
import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test

class TokenProcessorsTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.texts.TokenProcessors._

  object TkzrSpaceSep extends Tkzr with SpaceSepTkzr

  private val testData = Table(
    ("sentence", "tkzr", "expRes"),
    (
      "&#201;cole de Technologie Sup&#233;rieure (&#201;TS), Montreal",
      TkzrHtmlDecoder,
      IndexedSeq(
        "École", "de", "Technologie", "Supérieure", "(ÉTS),", "Montreal"
      )
    ),
    (
      "&#201;cole de Technologie Sup&#233;rieure (&#201;TS), Montreal",
      TkzrSpaceSep,
      IndexedSeq(
        "&#201;cole", "de", "Technologie", "Sup&#233;rieure", "(&#201;TS),", "Montreal"
      )
    )
  )

  @Test
  def spaceSepTest():Unit = {
    forAll(testData) { (sentence, tkzr, expRes) =>
      val tkns = IndexedSeq(textTkn(sentence))
      val res = tkzr.run(tkns)
      res.map(_.decodedOrText) shouldBe expRes
    }
  }
}
