package org.ditw.rschr

import java.io.{File, InputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.ditw.rschr.OrcidProfile_2015.{ActEid, ActivityExt, ProfileExt}
import org.ditw.rschr.utils.{SparkUtils, TgzUtils}


/**
  * Created by dev on 2017-11-13.
  */
object TraverseTgzs extends App {
  private def listFilesRecursively(folder:String):Iterable[String] = {
    val f = new File(folder)

    val files = f.listFiles().flatMap { ff =>
      if (ff.isFile) {
        if (ff.getName.toLowerCase().endsWith(".tgz")) Option(ff.getAbsolutePath)
        else None
      }
      else if (ff.isDirectory) {
        listFilesRecursively(ff.getAbsolutePath)
      }
      else {
        println(s"[${ff.getName}] is neither file or folder? Ignored")
        None
      }
    }

    files
  }

  private def processProfile(fn:String, is:InputStream):Option[String] = {
    val content = IOUtils.toString(is, StandardCharsets.UTF_8)
    val p = OrcidProfile_2015.readJson(content)

    if (p.profile != null) {
      val affInfo = p.profile.affInfoTrace
      val actInfo = p.profile.actInfoTrace

      if (p.profile.activityCount < 5)
        None
      else {
        val n = p.profile.name.getOrElse("[NoName]")
        Option(s"$n\n\t$affInfo\n\t$actInfo")
      }
    }
    else None //"[EmptyProfile]"
  }

  val spark = SparkUtils.sparkContextLocal()


  private def runRecursively(rootFolder:String):Unit = {
    val files = listFilesRecursively(rootFolder)


    val filtered = spark.parallelize(files.toSeq).flatMap { f =>
      val r = TgzUtils.processAllFiles(f, processProfile).flatten
      //println(r.flatten.mkString("\n"))
      r
    }

    val result = filtered.repartition(32)
    result.cache()
    println(s"Filtered total: ${result.count()}")

    result.saveAsTextFile("/media/sf_work/orcid_ext_2017")
  }

  private def extractProfile(fn:String, is:InputStream):Option[ProfileExt] = {
    val content = IOUtils.toString(is, StandardCharsets.UTF_8)
    val p = OrcidProfile_2015.readJson(content)

    if (p.profile != null) {
      val affInfo = p.profile.affInfoTrace
      val actInfo = p.profile.actInfoTrace

      if (p.profile.activityCount < 5)
        None
      else {
        val acts = p.profile.activities.works.work.map { w =>
          val t =
            if (w.work_title != null && w.work_title.title != null) {
              w.work_title.title.value
            }
            else "[NoTitle]"
          val eids:Array[ActEid] =
            if (w.work_ext_ids != null && w.work_ext_ids.ext_ids != null)
              w.work_ext_ids.ext_ids.map(eid => ActEid(eid.id_type, eid.id_value.value))
            else Array()
          ActivityExt(t, eids)
        }
        val ext = ProfileExt(
          p.profile.orcid_identifier.path,
          p.profile.name.getOrElse("[NoName]"),
          p.profile.affInfoTrace,
          acts
        )
        Option(ext)
      }
    }
    else None //"[EmptyProfile]"
  }

  private def extractRecursively(rootFolder:String):Unit = {
    val files = listFilesRecursively(rootFolder)

    val filtered = spark.parallelize(files.toSeq).flatMap { f =>
      val r = TgzUtils.processAllFiles(f, extractProfile).flatten
      //println(r.flatten.mkString("\n"))
      r
    }

    val result = filtered.repartition(256)
    result.cache()
    println(s"Filtered total: ${result.count()}")

    result.map(OrcidProfile_2015.profileExt2Json).saveAsTextFile("/media/sf_work/orcid_2016_ext")

  }

  extractRecursively("/media/sf_work/orcid_2016")

  spark.stop()
}
