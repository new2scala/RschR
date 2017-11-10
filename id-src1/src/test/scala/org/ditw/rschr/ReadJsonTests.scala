package org.ditw.rschr

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.ditw.rschr.utils.TgzUtils

object ReadJsonTests extends App {
  import OrcidProfile_2015._

  val otherFolder = "_other_"
  val nameFolderChars = 3
  val familyNamePartMaxChars = 10
  val rootFolder = "/media/sf_work/orcid_ext_2017"

  val InvalidFolderNames = Set("con", "aux")
  def fixFolderName(n:String):String = s"[$n]"
  def saveProfile(p:OrcidProfile2015, fileName:String, content:String):Unit = {
    if (p.profile != null) {
      val prf = p.profile
      if (prf != null && prf.bio != null && prf.bio.person_details != null) {
        val pd = prf.bio.person_details
        val gn = pd.given_names
        val fn = pd.family_name

        val fnFolderName =
          if (fn != null && fn.value != null && !fn.value.isEmpty) {
            var folderName =
              if (fn.value.length >= nameFolderChars) fn.value.substring(0, nameFolderChars).toLowerCase()
              else fn.value.toLowerCase()
            folderName = folderName.trim
            if (InvalidFolderNames.contains(folderName)) folderName = fixFolderName(folderName)
            var fnpart = if (fn.value.length > familyNamePartMaxChars) fn.value.substring(0, familyNamePartMaxChars) else fn.value
            fnpart = fnpart.trim.toLowerCase
            if (InvalidFolderNames.contains(fnpart)) folderName = fixFolderName(fnpart)
            s"$folderName/$fnpart"
          }
          else otherFolder

        var fullPath = s"$rootFolder/$fnFolderName".replace(' ', '_')

        var f = new File(fullPath)
        if (!f.exists()) {
          var r = f.mkdirs()
          if (!r) {
            println(s"Failed to create folder [$fullPath], putting in [$otherFolder] folder instead")
            f = new File(s"$rootFolder/$otherFolder")
            if (!f.exists()) {
              r = f.mkdirs()
              if (!r) println(s"Failed to create folder [$otherFolder] folder")
            }
          }
        }
        while (!f.exists()) Thread.sleep(200)

        val fo = new File(f, fileName)
        val fos = new FileOutputStream(fo)
        IOUtils.write(content, fos, StandardCharsets.UTF_8)
        fos.close()
      }
    }
    else {
      // nothing to save
    }
  }

  def parseOneRecord(fileName:String, json:String):String = {
//    val fin = new FileInputStream(recFile)
//    val j = IOUtils.toString(fin, StandardCharsets.UTF_8)
//    fin.close()
    val p = readJson(json)
    if (p.profile != null) {

      val lastSlash = fileName.lastIndexOf('/')
      val actualFileName = if (lastSlash >= 0) fileName.substring(lastSlash+1) else fileName
//      saveProfile(p, actualFileName, json)
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
      val r = parseOneRecord(fn, s)
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

  val p = "/media/sf_vmshare/ORCID_public_data_file_2016.tar.gz"
  // "/media/sf_vmshare/public_profiles_2017.tar.gz"
  //"/media/sf_vmshare/ORCID_public_data_file_2015.tar.gz"
  val allSummaries = TgzUtils.processTgz(
    p,
    s => s.endsWith(".json"),
    fileHandler
  )

  println(s"Total #: ${allSummaries.size}")

  val t = allSummaries.mkString("\n")

  val fo = new FileOutputStream("/media/sf_vmshare/ORCID_public_data_file_2016_summary.txt")
  IOUtils.write(t, fo, StandardCharsets.UTF_8)
  fo.close()

  //testOneRecord("/media/sf_vmshare/thomas_johnson.json")
}