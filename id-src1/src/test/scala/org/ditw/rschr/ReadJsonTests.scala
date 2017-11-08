package org.ditw.rschr

import java.io.{FileInputStream, InputStream}
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
    val act = p.profile.activities
    val actCount =
      if (act != null && act.works != null)
        p.profile.activities.works.work.length
      else 0
    val gn = p.profile.bio.person_details.given_names
    val fn = p.profile.bio.person_details.family_name
    s"$gn, $fn ($actCount)"
  }

  private def fileHandler(fn:String, is:InputStream):List[String] = {
    val s = IOUtils.toString(is, StandardCharsets.UTF_8)
    try {
      val r = parseOneRecord(s)
      println(s"$fn (${s.length}): $r")
      List(s)
    }
    catch {
      case t:Throwable => {
        println(s"failed to process [$fn]")
        throw t
      }
    }
  }

  val p = "/media/sf_vmshare/ORCID_public_data_file_2015.tar.gz"
  TgzUtils.processTgz(
    p,
    s => s.startsWith("./json/"),
    fileHandler
  )

  //testOneRecord("/media/sf_vmshare/thomas_johnson.json")
}