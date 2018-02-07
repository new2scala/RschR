package org.ditw

import org.ditw.rschr.utils.SparkUtils

/**
  * Created by dev on 2018-02-06.
  */
object SparkTestApp {
  def main(args: Array[String]): Unit = {
    val runLocally = if (args.length > 0) args(0).toBoolean else false

    val appName = s"Spark smoke test program (local run: $runLocally)"
    println(s"Starting $appName ...")


    val spark = SparkUtils.sparkContext(runLocally, appName)

    val testData = 0 to 100

    spark.parallelize(testData).foreach { d =>
      if (d % 2 == 0) println(d)
    }

    spark.stop

  }
}
