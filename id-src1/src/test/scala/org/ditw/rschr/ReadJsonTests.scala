package org.ditw.rschr

import java.io.{FileInputStream, FileOutputStream, InputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.ditw.rschr.utils.TgzUtils

object ReadJsonTests extends App {
  import OrcidProfile_2015._

  def parseOneRecord(json:String):String = {
//    val fin = new FileInputStream(recFile)
//    val j = IOUtils.toString(fin, StandardCharsets.UTF_8)
//    fin.close()
    val p = readJson(json)
    if (p.profile != null) {
      val prf = p.profile
      val act = prf.activities
      val actCount =
        if (act != null && act.works != null)
          act.works.work.length
        else 0

      if (prf != null && prf.bio != null && prf.bio.person_details != null) {
        val pd = prf.bio.person_details
        val gn = pd.given_names
        val fn = pd.family_name
        s"$gn, $fn ($actCount)"
      }
      else {
        s"[Unknown] ($actCount)"
      }
    }
    else s"[EmptyProfile]: ${p.error_desc}"
  }

  private var _count = 0L
  private def fileHandler(fn:String, is:InputStream):List[String] = {
    val s = IOUtils.toString(is, StandardCharsets.UTF_8)
    try {
      val r = parseOneRecord(s)
      val summary = s"$fn (${s.length}): $r"
      _count = _count+1
      if (_count % 500 == 0)
        println(_count)
      List(summary)
    }
    catch {
      case t:Throwable => {
        try {
          val deprecated = readDepRecJson(s)
          val tmp = s"deprecated profile: $fn"
          println(tmp)
          List(tmp)
        }
        catch {
          case t1:Throwable => {
            println(s"failed to process [$fn]")
            throw t
          }
        }

      }
    }
  }

  val p = "/media/sf_vmshare/public_profiles_2017.tar.gz"
  //"/media/sf_vmshare/ORCID_public_data_file_2015.tar.gz"
  val allSummaries = TgzUtils.processTgz(
    p,
    s => s.endsWith(".json"),
    fileHandler
  )

  println(s"${allSummaries.size}")

  val t = allSummaries.mkString("\n")

  val fo = new FileOutputStream("/media/sf_vmshare/ORCID_public_data_file_2017_summary.txt")
  IOUtils.write(t, fo, StandardCharsets.UTF_8)
  fo.close()

  //testOneRecord("/media/sf_vmshare/thomas_johnson.json")
}