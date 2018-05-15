package org.ditw.rschr

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

import com.intel.analytics.bigdl.dataset.{ByteRecord, DataSet}
import com.intel.analytics.bigdl.dataset.image.{BytesToGreyImg, GreyImgNormalizer, GreyImgToBatch}
import com.intel.analytics.bigdl.models.lenet.LeNet5
import com.intel.analytics.bigdl.nn._
import com.intel.analytics.bigdl.optim._
import com.intel.analytics.bigdl.utils.{Engine, File}
import com.intel.analytics.bigdl.visualization.{TrainSummary, ValidationSummary}
import org.apache.spark.SparkContext

/**
  * Created by dev on 2018-04-29.
  */
object TestBigDl extends App {

  case class TrainParams(
                          folder: String = "/home/dev/mnist",
                          checkpoint: Option[String] = None,
                          modelSnapshot: Option[String] = None,
                          stateSnapshot: Option[String] = None,
                          batchSize: Int = 10,
                          learningRate: Double = 0.005,
                          learningRateDecay: Double = 0.0,
                          maxEpoch: Int = 1,
                          coreNumber: Int = -1,
                          nodeNumber: Int = -1,
                          overWriteCheckpoint: Boolean = false,
                          graphModel: Boolean = false
                        )

  def load(featureFile: String, labelFile: String): Array[ByteRecord] = {

    val featureBuffer = if (featureFile.startsWith("hdfs:")) {
      ByteBuffer.wrap(File.readHdfsByte(featureFile))
    } else {
      ByteBuffer.wrap(Files.readAllBytes(Paths.get(featureFile)))
    }
    val labelBuffer = if (featureFile.startsWith("hdfs:")) {
      ByteBuffer.wrap(File.readHdfsByte(labelFile))
    } else {
      ByteBuffer.wrap(Files.readAllBytes(Paths.get(labelFile)))
    }
    val labelMagicNumber = labelBuffer.getInt()

    require(labelMagicNumber == 2049)
    val featureMagicNumber = featureBuffer.getInt()
    require(featureMagicNumber == 2051)

    val labelCount = labelBuffer.getInt()
    val featureCount = featureBuffer.getInt()
    require(labelCount == featureCount)

    val rowNum = featureBuffer.getInt()
    val colNum = featureBuffer.getInt()

    val result = new Array[ByteRecord](featureCount)
    var i = 0
    while (i < featureCount) {
      val img = new Array[Byte]((rowNum * colNum))
      var y = 0
      while (y < rowNum) {
        var x = 0
        while (x < colNum) {
          img(x + y * colNum) = featureBuffer.get()
          x += 1
        }
        y += 1
      }
      result(i) = ByteRecord(img, labelBuffer.get().toFloat + 1.0f)
      i += 1
    }

    result
  }

  val params = TrainParams()

  import Utils._

  val appName = "Train Lenet on MNIST"
  val conf = Engine.createSparkConf()
    .setAppName(appName)
    .set("spark.akka.frameSize", 64.toString)
    .set("spark.task.maxFailures", "1")
    .setMaster("local[*]")

  val sc = new SparkContext(conf)
  sc.setLogLevel("WARN")

  Engine.init

  val datafolder = "/home/dev/mnist"

  val trainData = params.folder + "/train-images-idx3-ubyte"
  val trainLabel = params.folder + "/train-labels-idx1-ubyte"
  val validationData = params.folder + "/t10k-images-idx3-ubyte"
  val validationLabel = params.folder + "/t10k-labels-idx1-ubyte"

  //val model = LeNet5(classNum = 10)

  val model = Sequential[Float]()
  model.add(Reshape(Array(28*28)))
    //.add(SpatialConvolution(1, 6, 5, 5))
    .add(Linear(28*28, 1000))
    .add(ReLU())
    .add(Linear(1000, 10))
    .add(LogSoftMax())

  val optMethod = new SGD[Float](
    learningRate = params.learningRate,
    learningRateDecay = params.learningRateDecay
  )

  val trainMean = 0.13066047740239506
  val trainStd = 0.3081078
  val trainSet = DataSet.array(load(trainData, trainLabel), sc) ->
    BytesToGreyImg(28, 28) -> GreyImgNormalizer(trainMean, trainStd) -> GreyImgToBatch(
    params.batchSize)

  val logDir = "/home/dev/tb"
  val trainSummary = TrainSummary(logDir, appName)
  val valSummary = ValidationSummary(logDir, appName)

  val optimizer = Optimizer(
    model = model,
    dataset = trainSet,
    criterion = ClassNLLCriterion[Float]()
  )
  optimizer.setTrainSummary(trainSummary)
  optimizer.setValidationSummary(valSummary)


  val testMean = 0.13251460696903547
  val testStd = 0.31048024

  val validationSet = DataSet.array(load(validationData, validationLabel), sc) ->
    BytesToGreyImg(28, 28) -> GreyImgNormalizer(testMean, testStd) -> GreyImgToBatch(
    params.batchSize)

  val trainedModel = optimizer.setValidation(
      trigger = Trigger.everyEpoch,
      dataset = validationSet,
      vMethods = Array(new Top1Accuracy, new Top5Accuracy[Float], new Loss[Float]))
    .setOptimMethod(optMethod)
    .setEndWhen(Trigger.maxEpoch(params.maxEpoch))
    .optimize()


  val validator = Validator(trainedModel, validationSet)
  val result = validator.test(Array(new Top1Accuracy[Float]))

  result.foreach(r => {
    println(s"${r._2} is ${r._1}")
  })

//  val model = Sequential[Double]()
//  model.add(Reshape(Array(1, 28, 28)))
//    .add(SpatialConvolution(1, 6, 5, 5))
//    .add(Tanh())
//    .add(SpatialMaxPooling(2, 2, 2, 2))
//    .add(Tanh())
//    .add(SpatialConvolution(6, 12, 5, 5))
//    .add(SpatialMaxPooling(2, 2, 2, 2))
//    .add(Reshape(Array(12 * 4 * 4)))
//    .add(Linear(12 * 4 * 4, 100))
//    .add(Tanh())
//    .add(Linear(100, 10))
//    .add(LogSoftMax())

  sc.stop()


}
