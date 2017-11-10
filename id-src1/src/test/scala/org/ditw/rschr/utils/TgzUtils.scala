package org.ditw.rschr.utils

import java.io._
import java.nio.charset.StandardCharsets

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream, TarArchiveOutputStream}
import org.apache.commons.compress.compressors.gzip.{GzipCompressorInputStream, GzipCompressorOutputStream}
import org.apache.commons.io.IOUtils

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-11-08.
  */
object TgzUtils {
  type FileHandler[T] = (String, InputStream) => T

  def processAllFiles[T](path:String, handler:FileHandler[T]):List[T] = {
    processTgz(path, s => true, handler)
  }

  val FHRead2String:FileHandler[String] = (fn:String, is:InputStream) => {
    IOUtils.toString(is, StandardCharsets.UTF_8)
  }

  def processTgz[T](path:String, nameFilter:String => Boolean, handler:FileHandler[T]):List[T] = {
    val bfFileInputStream = new BufferedInputStream(new FileInputStream(path))

    val tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(bfFileInputStream))
    var tarEntry = tarIn.getNextEntry

    var tarEntryIdx = 0
    val resultList = ListBuffer[T]()
    while (tarEntry != null) {
      val fileOrDir = if (tarEntry.isDirectory) "DIR" else "FILE"
      //println(s"Extracting [${tarEntry.getName}]($fileOrDir)")

      if (!tarEntry.isDirectory) {
        if (nameFilter(tarEntry.getName))
          resultList += handler(tarEntry.getName, tarIn)
        /*
        val bfos = new BufferedOutputStream(new FileOutputStream(f"E:\\VMShare\\tmp\\$tarEntryIdx%04d.json"))
        val bufSize = 4096
        val buf = new Array[Byte](bufSize)
        var cnt = tarIn.read(buf, 0, bufSize)
        while (cnt != -1) {
          bfos.write(buf, 0, cnt)
          cnt = tarIn.read(buf, 0, bufSize)
        }
        bfos.close()
        */
      }
      tarEntry = tarIn.getNextEntry
      tarEntryIdx = tarEntryIdx + 1
    }

    tarIn.close()
    resultList.toList
  }

  type FileNameContentHandler[T] = (String, InputStream) => T


  def processFilesWithNames[T](path:String, nameFilter:String => Boolean, handler:FileNameContentHandler[T]):List[T] = {
    val bfFileInputStream = new BufferedInputStream(new FileInputStream(path))

    val tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(bfFileInputStream))
    var tarEntry = tarIn.getNextEntry

    var tarEntryIdx = 0
    val resultList = ListBuffer[T]()
    while (tarEntry != null) {
      val fileOrDir = if (tarEntry.isDirectory) "DIR" else "FILE"
      //println(s"Extracting [${tarEntry.getName}]($fileOrDir)")

      if (!tarEntry.isDirectory) {
        if (nameFilter(tarEntry.getName))
          resultList += handler(tarEntry.getName, tarIn)
        /*
        val bfos = new BufferedOutputStream(new FileOutputStream(f"E:\\VMShare\\tmp\\$tarEntryIdx%04d.json"))
        val bufSize = 4096
        val buf = new Array[Byte](bufSize)
        var cnt = tarIn.read(buf, 0, bufSize)
        while (cnt != -1) {
          bfos.write(buf, 0, cnt)
          cnt = tarIn.read(buf, 0, bufSize)
        }
        bfos.close()
        */
      }
      tarEntry = tarIn.getNextEntry
      tarEntryIdx = tarEntryIdx + 1
    }

    tarIn.close()
    resultList.toList
  }

  def processGzFile[T](path:String, handler:FileHandler[T]):T = {
    val f = new File(path)
    val bfFileInputStream = new BufferedInputStream(new FileInputStream(path))
    val gzIn = new GzipCompressorInputStream(bfFileInputStream)
    val result = handler(f.getName, gzIn)
    gzIn.close()
    bfFileInputStream.close()
    result
  }

  def add2ExistingTgz(path:String, suffix:String, dstFile:String):Int = {
    val tarInFile = new File(dstFile)
    if (!tarInFile.exists) throw new IllegalArgumentException(s"Input Tgz file [$dstFile] not found!")

    val bstr = new ByteArrayOutputStream()
    val bfStream = new BufferedOutputStream(bstr)
    val tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))

    val existingNames:List[String] = processFilesWithNames(
      dstFile,
      (str:String) => true, // accept all the existing entries
      (fn:String, is:InputStream) => {
        val tgzEntry = new TarArchiveEntry(new File(fn), fn) // new TarArchiveEntry(f2Add, entryName)
        val bytes = IOUtils.toByteArray(is)
        tgzEntry.setSize(bytes.length.toLong)
        tgzOut.putArchiveEntry(tgzEntry)
        IOUtils.write(bytes, tgzOut)
        tgzOut.closeArchiveEntry()
        fn
      }
    )

    val lowercasedSet = existingNames.map(_.toLowerCase).toSet

    try {
      val files = new File(path).listFiles
      val files2Add:Array[File] = files.filter(_.getName.toLowerCase.endsWith(suffix))
      val newFiles = files2Add.filter(f => !lowercasedSet.contains(f.getName.toLowerCase))

      newFiles.foreach{ fAdd =>
        addFile2Tgz(tgzOut, path+fAdd.getName, "")
      }
      tgzOut.finish()
      tgzOut.close()
      if (newFiles.nonEmpty) {
        IOUtils.write(bstr.toByteArray, new FileOutputStream(dstFile))
      }
      newFiles.length
    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
        if (tgzOut != null) {
          tgzOut.close()
        }
        throw t
      }
    }
  }

  def addString2ExistingTgz(content:String, entryName:String, dstFile:String):Unit = {
    val bytes = content.getBytes(StandardCharsets.UTF_8)
    val bs = new ByteArrayInputStream(bytes)
    add2ExistingTgz(bs, entryName, bytes.length, dstFile)
    bs.close()
  }


  def add2ExistingTgz(contents:Iterable[(String, String)], dstFile:String):Unit = {
    val tarInFile = new File(dstFile)
    if (!tarInFile.exists) throw new IllegalArgumentException(s"Input Tgz file [$dstFile] not found!")

    val bstr = new ByteArrayOutputStream()
    val bfStream = new BufferedOutputStream(bstr)
    val tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))

    val existingNames:List[String] = processFilesWithNames(
      dstFile,
      (str:String) => true, // accept all the existing entries
      (fn:String, is:InputStream) => {
        val tgzEntry = new TarArchiveEntry(new File(fn), fn) // new TarArchiveEntry(f2Add, entryName)
        val bytes = IOUtils.toByteArray(is)
        tgzEntry.setSize(bytes.length.toLong)
        tgzOut.putArchiveEntry(tgzEntry)
        IOUtils.write(bytes, tgzOut)
        tgzOut.closeArchiveEntry()
        fn
      }
    )

    val lowercasedSet = existingNames.map(_.toLowerCase).toSet

    try {

      contents.foreach { p =>
        val entryName = p._1
        val json = p._2

        if (lowercasedSet.contains(entryName.toLowerCase()))
          println(s"[$entryName] already exists, will overwrite")
        val bytes = json.getBytes(StandardCharsets.UTF_8)
        val bs = new ByteArrayInputStream(bytes)
        add2Tgz(tgzOut, bs, entryName, bytes.length)
        bs.close()

      }

      tgzOut.finish()
      tgzOut.close()

      IOUtils.write(bstr.toByteArray, new FileOutputStream(dstFile))

    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
        if (tgzOut != null) {
          tgzOut.close()
        }
        throw t
      }
    }
  }

  def add2ExistingTgz(is:InputStream, entryName:String, size:Long, dstFile:String):Unit = {
    val tarInFile = new File(dstFile)
    if (!tarInFile.exists) throw new IllegalArgumentException(s"Input Tgz file [$dstFile] not found!")

    val bstr = new ByteArrayOutputStream()
    val bfStream = new BufferedOutputStream(bstr)
    val tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))

    val existingNames:List[String] = processFilesWithNames(
      dstFile,
      (str:String) => true, // accept all the existing entries
      (fn:String, is:InputStream) => {
        val tgzEntry = new TarArchiveEntry(new File(fn), fn) // new TarArchiveEntry(f2Add, entryName)
        val bytes = IOUtils.toByteArray(is)
        tgzEntry.setSize(bytes.length.toLong)
        tgzOut.putArchiveEntry(tgzEntry)
        IOUtils.write(bytes, tgzOut)
        tgzOut.closeArchiveEntry()
        fn
      }
    )

    val lowercasedSet = existingNames.map(_.toLowerCase).toSet

    try {

      if (lowercasedSet.contains(entryName.toLowerCase()))
        println(s"[$entryName] already exists, will overwrite")
      add2Tgz(tgzOut, is, entryName, size)
      tgzOut.finish()
      tgzOut.close()

      IOUtils.write(bstr.toByteArray, new FileOutputStream(dstFile))

    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
        if (tgzOut != null) {
          tgzOut.close()
        }
        throw t
      }
    }
  }

  def createTgz(path:String, suffix:String, dstFile:String):Unit = {
    var tgzOut:TarArchiveOutputStream = null
    try {
      val bfStream = new BufferedOutputStream(new FileOutputStream(dstFile))
      tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))
      val files = new File(path).listFiles
      val files2Add = files.filter(_.getName.toLowerCase.endsWith(suffix))
      files2Add.foreach{ fAdd =>
        addFile2Tgz(tgzOut, path+fAdd.getName, "")
      }
    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
      }
    }
    finally {
      if (tgzOut != null) {
        tgzOut.finish()
        tgzOut.close()
      }
    }
  }

  def createTgz(is:InputStream, entryName:String, size:Long, dstFile:String):Unit = {
    var tgzOut:TarArchiveOutputStream = null
    try {
      val bfStream = new BufferedOutputStream(new FileOutputStream(dstFile))
      tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))
      add2Tgz(tgzOut, is, entryName, size)
    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
      }
    }
    finally {
      if (tgzOut != null) {
        tgzOut.finish()
        tgzOut.close()
      }
    }
  }

  def createTgz(contents:Iterable[(String, String)], dstFile:String):Unit = {
    var tgzOut:TarArchiveOutputStream = null
    try {
      val bfStream = new BufferedOutputStream(new FileOutputStream(dstFile))
      tgzOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(bfStream))
      contents.foreach { p =>
        val entryName = p._1
        val json = p._2
        val bytes = json.getBytes(StandardCharsets.UTF_8)
        val bs = new ByteArrayInputStream(bytes)
        add2Tgz(tgzOut, bs, entryName, bytes.length)
        bs.close()
      }
    }
    catch {
      case t:Throwable => {
        println(s"Error creating tgz archive: ${t.getMessage}")
      }
    }
    finally {
      if (tgzOut != null) {
        tgzOut.finish()
        tgzOut.close()
      }
    }
  }

  def createTgzFromString(content:String, entryName:String, dstFile:String):Unit = {
    val bytes = content.getBytes(StandardCharsets.UTF_8)
    val bs = new ByteArrayInputStream(bytes)
    createTgz(bs, entryName, bytes.length, dstFile)
    bs.close()
  }

  def add2Tgz(tarOut:TarArchiveOutputStream, is2Add:InputStream, entryName:String):Unit = {
    val tarEntry = new TarArchiveEntry(entryName)
    tarOut.putArchiveEntry(tarEntry)
    IOUtils.copy(is2Add, tarOut)
    tarOut.closeArchiveEntry()
  }
  def add2Tgz(tarOut:TarArchiveOutputStream, is2Add:InputStream, entryName:String, size:Long):Unit = {
    val tarEntry = new TarArchiveEntry(entryName)
    tarEntry.setSize(size)
    tarOut.putArchiveEntry(tarEntry)
    IOUtils.copy(is2Add, tarOut)
    tarOut.closeArchiveEntry()
  }


  private def addFile2Tgz(tarOut:TarArchiveOutputStream, toAdd:String, entryBase:String, size:Long):Unit = {
    val f2Add = new File(toAdd)
    if (f2Add.exists) {
      val entryName = entryBase+f2Add.getName
      val fs = new FileInputStream(f2Add)
      add2Tgz(tarOut, fs, entryName, size)
      fs.close
      tarOut.closeArchiveEntry()
    }
  }
  private def addFile2Tgz(tarOut:TarArchiveOutputStream, toAdd:String, entryBase:String):Unit = {
    val f2Add = new File(toAdd)
    if (f2Add.exists) {
      val entryName = entryBase+f2Add.getName
      val fs = new FileInputStream(f2Add)
      add2Tgz(tarOut, fs, entryName)
      fs.close
      tarOut.closeArchiveEntry()
    }
  }
}
