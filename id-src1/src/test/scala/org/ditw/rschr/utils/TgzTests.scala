package org.ditw.rschr.utils

import java.io.InputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils

/**
  * Created by dev on 2017-11-08.
  */
object TgzTests extends App {

  private def fileHandler(fn:String, is:InputStream):List[String] = {
    val s = IOUtils.toString(is, StandardCharsets.UTF_8)
    println(s"$fn: ${s.length}")
    List(s)
  }

  val p = "/media/sf_vmshare/ORCID_public_data_file_2015.tar.gz"
  TgzUtils.processAllFiles(
    p,
    fileHandler
  )
}
