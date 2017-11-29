package org.ditw.rschr

import org.apache.spark.rdd.RDD
import org.ditw.rschr.utils.SparkUtils

/**
  * Created by dev on 2017-11-29.
  */
object WorksExtractor extends App {

  val dataPaths = List(
    "/media/sf_work/orcid_2017_works"
//    "/media/sf_work/orcid_2016_works",
//    "/media/sf_work/orcid_2015_works"
  )

  case class ExpertPmids(eid:String, pmids:Set[String])

  private def extractWorks(workIdType:String, dataPath:String) = {


    val trace = spark.textFile(dataPath).map(OrcIdWorks.expWorkIdsFromJson)
      .flatMap { e =>
        val ids = e.wids.filter(_.t.toLowerCase == workIdType).flatMap(_.v)
        if (ids.nonEmpty) {
          if (e.name.nonEmpty) Option(e.name.get -> s"(${ids.length}): ${ids.mkString(",")}")
          else None
        }
        else None
      }

//    val experts = trc.count()
//    val pmids = trc.map(_._2).sum()
//    println(s"$dataPath: $experts, $pmids")
    trace
  }

  val spark = SparkUtils.sparkContextLocal()

  var r = extractWorks("pmid", dataPaths(0))

  val experts = r.count()
  println(experts)

  r.coalesce(64).sortBy(_._1.toLowerCase).saveAsTextFile("/media/sf_work/orcid_2017_trace")

  spark.stop()
}
