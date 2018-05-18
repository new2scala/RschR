package org.ditw.texts

object InverseIndice extends Serializable {

  type Eid = Long
  type TokenType = String
  trait TEntity extends Serializable {
    val id:Eid
    val tokens:IndexedSeq[TokenType]
  }

  // https://en.wikipedia.org/wiki/Tf%E2%80%93idf
  def idfMap(entities:Iterable[TEntity]):Map[TokenType,Double] = {
    val tokenOcc = entities.flatMap { e =>
        e.tokens.distinct.map(_ -> 1)
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum)

    val res = tokenOcc.map(p => p._1 -> math.log(entities.size*1.0/p._2))
    res
  }

  def relIdf(idf:Map[TokenType,Double], entity:TEntity):Map[TokenType,Double] = {
    val distinctTokens = entity.tokens.distinct

    val idfSum = distinctTokens.map(idf).sum

    val res = distinctTokens.map(t => t -> idf(t)/idfSum)
    res.toMap
  }

  private def relIdfKeepP(m:Map[TokenType,Double], p:Double):Map[TokenType,Double] = {
    m.filter(_._2 >= p)
  }

  def inverseIndex(entities:Iterable[TEntity], cutoff:Double):(Map[Eid, Map[TokenType,Double]], Map[TokenType,Set[Eid]]) = {

    val idfm = idfMap(entities)
    val id2RelWeight:Map[Eid, Map[TokenType,Double]] = entities.map { e =>
        val relm = relIdf(idfm, e)
        val res = relIdfKeepP(relm, cutoff)
        e.id -> res
      }.toMap

    val invMap = id2RelWeight.toIndexedSeq.flatMap { p =>
        p._2.keys.map(t => t -> p._1)
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2).toSet)
    id2RelWeight -> invMap
  }

  private val EmptyCandidateSet = Set[Eid]()

  def findCandidates(entity: TEntity, invMap:Map[TokenType,Set[Eid]], relWeightMap:Map[Eid, Map[TokenType,Double]])
    :IndexedSeq[(Eid,Double)] = {
    val distinctTokens = entity.tokens.distinct
    val allCand = distinctTokens.flatMap(t => invMap.getOrElse(t, EmptyCandidateSet)).toSet
    allCand.map { cand =>
      val relwm = relWeightMap(cand)
      val sumWeight = distinctTokens.flatMap(relwm.get).sum
      cand -> sumWeight
    }
    .toIndexedSeq
    .sortBy(_._2)
  }

//  def idfWeight(entity:TEntity, idfm:Map[TokenType,Double]):Double = {
//    entity.tokens.flatMap(idfm.get).sum
//  }
}