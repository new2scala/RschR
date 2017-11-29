package org.ditw.rschr

import org.json4s.DefaultFormats

/**
  * Created by dev on 2017-11-27.
  */
object OrcIdWorks {
  case class WorkInfo(
                     title:String,
                     eids:Map[String,String]
                     ) {
    def pmid:Option[String] = eids.get("pmid")
    def doi:Option[String] = eids.get("doi")
    def pii:Option[String] = eids.get("pii")
  }

  case class WorkIds(t:String, v:Array[String])
  case class ExpWorkIds(id:String,
                       name:Option[String],
                       wc:Int,
                       wids:Array[WorkIds]
                       )

  case class Id2WorkIds(
                       id:String,
                       name:Option[String],
                       works:Array[WorkInfo]
                       ) {
    private def getPmids = works.flatMap(_.pmid).map(_.trim.toLong).sorted
    private def getDois = works.flatMap(_.doi).sorted
    private def getPiis = works.flatMap(_.pii).sorted
    override def toString:String = {
      val n = name.getOrElse(id)
      val workCount = works.length
      val pmids:Array[Long] = getPmids
      val pmidCount = pmids.length
      val pmidTrace = pmids.mkString(",")
      val dois = getDois
      val doiCount = dois.length
      val doiTrace = dois.mkString(",")
      s"[$n]($workCount) pmids($pmidCount): [$pmidTrace], dois($doiCount): [$doiTrace]"
    }

    def toExpWorkIds:ExpWorkIds = {
      val wids = works.flatMap(_.eids.toList).groupBy(_._1.trim)
        .map(p => WorkIds(p._1, p._2.map(_._2.trim)))
        .toArray.sortBy(_.t)
      ExpWorkIds(id, name, works.length, wids)
    }
  }

  def expWorkIds2Json(ew:ExpWorkIds):String = {
    import org.json4s.jackson.Serialization._
    implicit val fmt = DefaultFormats
    write(ew)
  }

  def expWorkIdsFromJson(j:String):ExpWorkIds = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = DefaultFormats
    parse(j).extract[ExpWorkIds]
  }
}
