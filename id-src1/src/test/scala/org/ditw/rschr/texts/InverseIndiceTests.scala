package org.ditw.texts

import org.ditw.texts.InverseIndice.{Eid, TEntity, TokenType}
import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test

class InverseIndiceTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  private def createEntities(ents:String*):Iterable[TEntity] = {
    var idx = 0
    ents.map { e =>
      val eid = idx.toLong
      idx += 1
      new TEntity {
        override val tokens: IndexedSeq[TokenType] = e.split("\\s+")
        override val id: Eid = eid
      }
    }
  }

  private val testData = Table(
    ("entities1", "entities2", "expIDFMap"),
    (
      createEntities(
        "University of Arkansas System",
        "University of Arkansas for Medical Sciences",
        "Arkansas State University",
        "Arkansas Tech University",
        "Henderson State University",
        "John Brown University",
        "Ouachita Baptist University",
        "Southern Arkansas University",
        "University of Arkansas – Fort Smith",
        "University of Arkansas at Little Rock",
        "University of Arkansas at Monticello",
        "University of Arkansas at Pine Bluff",
        "University of Central Arkansas",
        "Harding University Main Campus",
        "University of Arkansas at Fayetteville",
        "University of Arkansas Medical Center",
        "University of the Ozarks",
        "University of Arkansas Community College at Batesville",
        "Arkansas State University Mountain Home",
        "Phillips Community College of the University of Arkansas",
        "Southern Arkansas University Tech",
        "University of Arkansas Community College at Hope",
        "University of Arkansas Community College at Morrilton",
        "University of Phoenix",
        "Arkansas State University System"
      ),
      createEntities(
        "Little Rock",
        "Little NoiValey",
        "Jonesboro",
        "Russellville",
        "Arkadelphia",
        "Siloam Springs",
        "Magnolia",
        "Fort Smith",
        "Smith NoiValey",
        "Monticello",
        "Pine Bluff",
        "Conway",
        "Searcy",
        "Fayetteville",
        "Clarksville",
        "Batesville",
        "Mountain Home",
        "Helena-West Helena",
        "Camden",
        "Hope",
        "Morrilton"
      ),
      Map[TokenType,Double]()
    )
  )

  @Test
  def test1():Unit = {
    forAll(testData) { (entities1, entities2, expIDFMap) =>
//      val m = InverseIndice.idfMap(entities1)
//      println(m.size)
//
//      val weighedIdf = entities.map(e => InverseIndice.relIdf(m, e))
//      println(weighedIdf.size)

      val (relWeightMap1, invMap1) = InverseIndice.inverseIndex(entities1, 0.1)
      println(invMap1.size)

      val testEnt1 = new TEntity {
        override val tokens: IndexedSeq[TokenType] = "University of Arkansas for Medical Sciences (UAMS)".split("\\s+")
        override val id: Eid = 100000L
      }
      val testEnt2 = new TEntity {
        override val tokens: IndexedSeq[TokenType] = "University of Arkansas at Little Rock".split("\\s+")
        override val id: Eid = 100000L
      }
      val testEnt3= new TEntity {
        override val tokens: IndexedSeq[TokenType] = "University of Arkansas".split("\\s+")
        override val id: Eid = 100000L
      }

      val cand1 = InverseIndice.findCandidates(testEnt1, invMap1, relWeightMap1)
      val cand2 = InverseIndice.findCandidates(testEnt2, invMap1, relWeightMap1)
      val cand3 = InverseIndice.findCandidates(testEnt3, invMap1, relWeightMap1)

//      val idfw1 = InverseIndice.idfWeight(testEnt1, idf1)
//      val idfw2 = InverseIndice.idfWeight(testEnt2, idf1)

      val (relWeightMap2, invMap2) = InverseIndice.inverseIndex(entities2, 0.2)
      println(invMap2.size)
    }

  }
}