package org.ditw.rschr

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.ditw.rschr.utils.{SparkUtils, TgzUtils}
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer

object ReadJsonTests extends App {
  import OrcidProfile_2015._

  val otherFolder = "__other__"
  val otherTgzName = "[other]"
  val emptyTgzName = "[empty]"
  val nameFolderChars = 3
  val familyNamePartMaxChars = 10
  val rootFolder = "/media/sf_work/orcid_ext_2016"

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
            folderName
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

        var tgzName =
          if (fn != null && fn.value != null) {
            if (fn.value.length > familyNamePartMaxChars) fn.value.substring(0, familyNamePartMaxChars) else fn.value
          }
          else emptyTgzName
        tgzName = tgzName.trim.toLowerCase.replace(' ', '_')
        //if (InvalidFolderNames.contains(tgzName)) folderName = fixFolderName(fnpart)


          val fo = new File(f, tgzName)

          if (!fo.exists()) {
            TgzUtils.createTgzFromString(content, fileName, fo.getAbsolutePath)
          }
          else {
            TgzUtils.addString2ExistingTgz(content, fileName, fo.getAbsolutePath)
          }

          if (!fo.exists()) {
            println(s"Failed to create/append file [${f.getAbsolutePath}/$tgzName]")
            val fo1 = new File(f, otherTgzName)
            println(s"\tTrying to put in ${fo1.getAbsolutePath}")
            if (!fo1.exists()) {
              TgzUtils.createTgzFromString(content, fileName, fo1.getAbsolutePath)
            }
            else {
              TgzUtils.addString2ExistingTgz(content, fileName, fo1.getAbsolutePath)
            }
          }

//        val fos = new FileOutputStream(fo)
//        IOUtils.write(content, fos, StandardCharsets.UTF_8)
//        fos.close()
      }
    }
    else {
      // nothing to save
    }
  }

  def extractOneRecord(fileName:String, json:String):(String, String, OrcidProfile2015) = {
    //    val fin = new FileInputStream(recFile)
    //    val j = IOUtils.toString(fin, StandardCharsets.UTF_8)
    //    fin.close()
    val p = readJson(json)
    val lastSlash = fileName.lastIndexOf('/')
    val actualFileName = if (lastSlash >= 0) fileName.substring(lastSlash+1) else fileName
    (actualFileName, json, p)
  }

  def parseOneRecord(fileName:String, json:String):String = {
//    val fin = new FileInputStream(recFile)
//    val j = IOUtils.toString(fin, StandardCharsets.UTF_8)
//    fin.close()
    val p = readJson(json)
    if (p.profile != null) {

      val lastSlash = fileName.lastIndexOf('/')
      val actualFileName = if (lastSlash >= 0) fileName.substring(lastSlash+1) else fileName
      saveProfile(p, actualFileName, json)
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

  case class EntrySaveInfo(folderName:String, tgzFileName:String, nameInTgz:String, content:String) {
    private[ReadJsonTests] def getKey:String = s"$folderName/$tgzFileName"
  }

  private def toEntry(nameInTgz:String, content:String, prf:OrcidProfile2015):EntrySaveInfo = {
    if (prf.profile != null && prf.profile.bio != null && prf.profile.bio.person_details != null) {
      val pd = prf.profile.bio.person_details
      val gn = pd.given_names
      val fn = pd.family_name
      val fnFolderName =
        if (fn != null && fn.value != null && !fn.value.isEmpty) {
          var folderName =
            if (fn.value.length >= nameFolderChars) fn.value.substring(0, nameFolderChars).toLowerCase()
            else fn.value.toLowerCase()
          folderName = folderName.trim
          if (InvalidFolderNames.contains(folderName)) folderName = fixFolderName(folderName)
          folderName
        }
        else otherFolder
      var tgzName =
        if (fn != null && fn.value != null) {
          if (fn.value.length > familyNamePartMaxChars) fn.value.substring(0, familyNamePartMaxChars) else fn.value
        }
        else emptyTgzName
      tgzName = tgzName.trim.toLowerCase.replace(' ', '_')

      EntrySaveInfo(fnFolderName, tgzName, nameInTgz, content)
    }
    else {
      EntrySaveInfo(otherFolder, emptyTgzName, nameInTgz, content)
    }
  }

  private def findNextFileName(folderName:String, tgzName:String):String = {
    val folderPath = s"$rootFolder/$folderName"
    var fileIndex = 1
    var tgzFileName = f"$folderPath/$tgzName-$fileIndex%04d.tgz"
    var tgzFile = new File(tgzFileName)
    while (tgzFile.exists()) {
      fileIndex = fileIndex+1
      tgzFileName = f"$folderPath/$tgzName-$fileIndex%04d.tgz"
      tgzFile = new File(tgzFileName)
    }
    tgzFileName
  }

  private def saveAsOther(content:Array[Byte]):Unit = {
    val tgzFileName2 = findNextFileName(otherFolder, otherTgzName)
    val of2 = new File(tgzFileName2)
    val ofs2 = new FileOutputStream(tgzFileName2)
    IOUtils.write(content, ofs2)
    ofs2.close()

    if (!of2.exists())
      println(s"\tFailed to create [$tgzFileName2], abort saving!")
  }

  def saveOne(fn:String, content:Array[Byte]):Unit = {
    val firstSlash = fn.indexOf('/')
    val folderName = fn.substring(0, firstSlash)
    val tgzName = fn.substring(firstSlash+1)

    val tgzFileName = findNextFileName(folderName, tgzName)
    val folderPath = new File(s"$rootFolder/$folderName")
    if (!folderPath.exists()) folderPath.mkdirs()

    if (folderPath.exists()) {
      val of = new File(tgzFileName)
      val ofs = new FileOutputStream(tgzFileName)
      IOUtils.write(content, ofs)
      ofs.close()

      if (!of.exists()) {
        // failed somehow
        println(s"Failed to create [$tgzFileName], try saving it in 'other' folder")
        saveAsOther(content)
      }
    }
    else {
      println(s"Failed to create folder [$folderPath], try saving it in 'other' folder")
      saveAsOther(content)
    }

  }


  import collection.mutable

  def saveTopN(tgzName2Bytes:mutable.Map[String,Array[Byte]], percentage:Double):mutable.Map[String,Array[Byte]] = {
    val c = (percentage*tgzName2Bytes.size).toInt
    println(f"Saving top $percentage%.3f of the remaining data (#: $c/${tgzName2Bytes.size})...")
    val sorted = tgzName2Bytes.toList.sortBy(-_._2.length)

    val rem = mutable.Map[String,Array[Byte]]()
    var idx = 0
    sorted.foreach { p =>
      val k = p._1
      val s = p._2
      idx = idx + 1
      if (idx <= c) {
        saveOne(k, s)
        if (idx % 100 == 0) println(s"\t$idx saved")
      }
      else rem += k -> s

    }
    rem
  }

  private val SaveFileSizeThreshold = 102400
  private val ThresholdCount_SavePercentage = 100000
  private val SavePercentage = 0.01
  private val spark = SparkUtils.sparkContextLocal()
  private def batchSave(
                         records:mutable.Map[String, ListBuffer[EntrySaveInfo]],
                         tgzName2Bytes:mutable.Map[String,Array[Byte]]
                       ):mutable.Map[String,Array[Byte]] = {
    val keys = records.keySet.toSeq
    val recs:Map[String, Array[EntrySaveInfo]] = records.map(p => p._1 -> p._2.toArray).toMap
    val brRecords = spark.broadcast(recs)
    val newName2Bytes = spark.parallelize(keys).map { k =>
      val entries = brRecords.value(k)
      val bs = TgzUtils.createInMemTgz(entries.map(e => e.nameInTgz -> e.content))
      val bytes = bs.toByteArray
      bs.close()
      k -> bytes
    }.collect().toMap

    newName2Bytes.keys.foreach { k =>
      if (tgzName2Bytes.contains(k)) {
        val merged = TgzUtils.mergeExistingTgzStreams(tgzName2Bytes(k), newName2Bytes(k))
        tgzName2Bytes += k -> merged
      }
      else tgzName2Bytes += k -> newName2Bytes(k)
    }

    println(s"Before saving #: ${tgzName2Bytes.size}")

    if (tgzName2Bytes.size > ThresholdCount_SavePercentage)
      saveTopN(tgzName2Bytes, SavePercentage)
    else {
      val rem = mutable.Map[String,Array[Byte]]()
      tgzName2Bytes.keys.foreach { k =>
        val s = tgzName2Bytes(k)
        if (s.length >= SaveFileSizeThreshold) {
          saveOne(k, s)
          println(s"Data for key [$k] saved")
        }
        else {
          rem += k -> s
        }
      }
      println(s"After saving #: ${rem.size}")
      rem
    }
//      val entries = brRecords.value(k)
//      //println(entries.length)
//      val folderName = entries.head.folderName
//      val tgzName = entries.head.tgzFileName
//      var tgzFileName = s"$rootFolder/$folderName/$tgzName"
//      val tgzFile = new File(tgzFileName)
//      if (tgzFile.exists()) {
//        TgzUtils.add2ExistingTgz(
//          entries.map(e => e.nameInTgz -> e.content),
//          tgzFileName
//        )
//      }
//      else {
//        var folder = new File(s"$rootFolder/$folderName")
//        if (!folder.exists()) folder.mkdirs()
//
//        if (!folder.exists()) {
//          val ofolder = s"$rootFolder/$otherFolder"
//          println(s"Cannot create folder [${folder.getAbsolutePath}], putting in [$ofolder] instead")
//
//          folder = new File(ofolder)
//          folder.mkdirs()
//          if (!folder.exists()) {
//            throw new RuntimeException(s"Failed to create other folder [$otherFolder]")
//          }
//          tgzFileName = s"$ofolder/$tgzName"
//        }
//
//        TgzUtils.createTgz(entries.map(e => e.nameInTgz -> e.content), tgzFileName)
//
//        if (!new File(tgzFileName).exists()) {
//          val ofile = s"${folder.getAbsolutePath}/$otherTgzName"
//          println(s"Cannot create file [$tgzFileName], putting in [$ofile] instead")
//
//          val of = new File(ofile)
//          if (of.exists()) {
//            TgzUtils.add2ExistingTgz(
//              entries.map(e => e.nameInTgz -> e.content),
//              of.getAbsolutePath
//            )
//          }
//          else {
//            TgzUtils.createTgz(
//              entries.map(e => e.nameInTgz -> e.content),
//              of.getAbsolutePath
//            )
//          }
//        }
//      }

//    }
  }

  case class BatchInfo(size:Int) {
    private var _tgzName2Profiles = mutable.Map[String, ListBuffer[EntrySaveInfo]]()
    private var _tgzName2Bytes = mutable.Map[String,Array[Byte]]()
    private var _countInBatch = 0
    private var _prevTs = DateTime.now()

    def addAndCheck(fn:String, json:String, prf:OrcidProfile2015):Unit = {
      val e = toEntry(fn, json, prf)
      val k = e.getKey
      if (!_tgzName2Profiles.contains(k)) _tgzName2Profiles += e.getKey -> ListBuffer()
      _tgzName2Profiles(k) += e

      _countInBatch = _countInBatch+1
      if (_countInBatch % size == 0) {
        val currTs = DateTime.now()
        val tsDiff = currTs.getMillis - _prevTs.getMillis
        _prevTs = currTs
        println(f"Processing batch ${_countInBatch} (${tsDiff/1000.0}%.2f sec)")
        val tgzFiles = _tgzName2Profiles.size
        val entries = _tgzName2Profiles.map(_._2.size).sum
        println(s"\tSaving in $tgzFiles files ($entries entries)")

        _tgzName2Bytes = batchSave(_tgzName2Profiles, _tgzName2Bytes)

        _tgzName2Profiles = mutable.Map[String, ListBuffer[EntrySaveInfo]]()
      }
    }

    def saveAllRem():Unit = {
      var count = 0
      println(s"Saving all the remaining data (#: ${_tgzName2Bytes.size})...")
      _tgzName2Bytes.keys.foreach { k =>
        val s = _tgzName2Bytes(k)
        saveOne(k, s)

        count = count + 1
        if (count % 500 == 0) println(s"\t$count saved")
      }
    }
  }

  private var batchSize = 10000
  private val _batchInfo = BatchInfo(batchSize)
  private def fileHandlerBatchSave(fn:String, is:InputStream):List[String] = {
    val s = IOUtils.toString(is, StandardCharsets.UTF_8)
    try {
      val (n, json, prf) = extractOneRecord(fn, s)
      _batchInfo.addAndCheck(n, json, prf)
      List()
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
    fileHandlerBatchSave
    //fileHandler
  )

  spark.stop()

//  println(s"Total #: ${allSummaries.size}")
//
//  val t = allSummaries.mkString("\n")
//
//  val fo = new FileOutputStream("/media/sf_vmshare/ORCID_public_data_file_2016_summary.txt")
//  IOUtils.write(t, fo, StandardCharsets.UTF_8)
//  fo.close()

  //testOneRecord("/media/sf_vmshare/thomas_johnson.json")
}