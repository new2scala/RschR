package org.ditw.rschr.utils

import java.io.{ByteArrayInputStream, FileOutputStream, InputStream}
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

//  val p = "/media/sf_vmshare/ORCID_public_data_file_2015.tar.gz"
//  TgzUtils.processAllFiles(
//    p,
//    fileHandler
//  )

  val towrite = List(
    ("file1.json", "{ this is a test file #1, \n\tline2\n}"),
    ("file2.json", "{ this is a test file #2, \n\tline2\n}")
  )

  val bs = TgzUtils.createInMemTgz(towrite)

  val bytes = bs.toByteArray
  val bs2 = TgzUtils.add2ExistingTgzStream(
    List(
      ("file3", "{ this is a test file #3, \n\tline2\n}"),
      ("file4.json", "{ this is a test file #4, \n\tline2\n}")
    ), bytes
  )
  val bis = new ByteArrayInputStream(bs2)
  IOUtils.copy(bis, new FileOutputStream("/media/sf_work/inmem.tgz"))
}
