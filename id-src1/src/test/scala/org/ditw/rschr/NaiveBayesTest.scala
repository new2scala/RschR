package org.ditw.rschr

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.ditw.rschr.utils.SparkUtils

/**
  * Created by dev on 2018-01-31.
  */
object NaiveBayesTest extends App {

  val spark = SparkUtils.sparkContextLocal()

  private def processLine(line:String, filterStr:String):Option[String] = {
    val sp = line.split("\t")
    if (sp(0) == filterStr) Option(sp(1))
    else None
  }

  private def loadData = {
    val path = "file:///media/sf_vmshare/smsspamcollection"
    val allData = spark.textFile(path)

    val spams = allData.flatMap(line => processLine(line, "spam"))
    val hams = allData.flatMap(line => processLine(line, "ham"))

    spams -> hams
  }

  val (spams, hams) = loadData

  val tf = new HashingTF(numFeatures = 200)

  val spamFeats = spams.map(l => tf.transform(l.split(" ")))
  val hamFeats = hams.map(l => tf.transform(l.split(" ")))

  val posData = spamFeats.map(feat => LabeledPoint(1, feat))
  val negData = hamFeats.map(feat => LabeledPoint(0, feat))

  val allData = posData.union(negData).cache()

  val dataSets = allData.randomSplit(Array(0.6, 0.4))

  val (trainingSet, testSet) = dataSets(0) -> dataSets(1)
  val nbModel = NaiveBayes.train(trainingSet, 1.0)

  val predicted = testSet.map(d => nbModel.predict(d.features) -> d.label)

  val tr = predicted.filter(p => p._1 == p._2).count()
  val acc = tr * 100.0 / predicted.count()
  println(f"$acc%.3f%%")

  spark.stop()
}
