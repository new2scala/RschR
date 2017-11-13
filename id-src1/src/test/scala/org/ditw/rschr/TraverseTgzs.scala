package org.ditw.rschr

import java.io.{File, InputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
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

  private def runRecursively(rootFolder:String):Unit = {
    val files = listFilesRecursively(rootFolder)

    val spark = SparkUtils.sparkContextLocal()


    val filtered = spark.parallelize(files.toSeq).flatMap { f =>
      val r = TgzUtils.processAllFiles(f, processProfile).flatten
      //println(r.flatten.mkString("\n"))
      r
    }

    val result = filtered.repartition(32)
    result.cache()
    println(s"Filtered total: ${result.count()}")

    result.saveAsTextFile("/media/sf_work/orcid_o1")

    spark.stop()
  }

  runRecursively("/media/sf_work/orcid_2015")
}
