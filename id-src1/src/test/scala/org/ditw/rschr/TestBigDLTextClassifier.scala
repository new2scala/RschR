package org.ditw.rschr

import java.io.File

import com.intel.analytics.bigdl.example.textclassification.TextClassifier
import com.intel.analytics.bigdl.example.utils._
import com.intel.analytics.bigdl.nn._
import com.intel.analytics.bigdl.utils.Engine
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.io.Source
import SimpleTokenizer._
import com.intel.analytics.bigdl.Module
import com.intel.analytics.bigdl.dataset.Sample
import com.intel.analytics.bigdl.optim.{Adagrad, Optimizer, Top1Accuracy, Trigger}
import com.intel.analytics.bigdl.tensor.Tensor
/**
  * Created by dev on 2018-05-01.
  */
object TestBigDLTextClassifier extends App {

//
//  case class TextClassificationParams(override val baseDir: String = "./",
//                                      override val maxSequenceLength: Int = 500,
//                                      override val maxWordsNum: Int = 5000,
//                                      override val trainingSplit: Double = 0.8,
//                                      override val batchSize: Int = 128,
//                                      override val embeddingDim: Int = 200,
//                                      override val learningRate: Double = 0.01,
//                                      override val partitionNum: Int = 4)
//    extends AbstractTextClassificationParams

//  val appName = "Test Classifier test"
//  val conf = Engine.createSparkConf()
//    .setAppName(appName)
//    .set("spark.akka.frameSize", 64.toString)
//    .set("spark.task.maxFailures", "1")
//    .setMaster("local[*]")
//
//  val sc = new SparkContext(conf)
//  sc.setLogLevel("WARN")
//
//  Engine.init

  val param = TextClassificationParams(
    baseDir = "/media/sf_work/baseDir",
    partitionNum = 4096
  )

  println(s"Current parameters: $param")

  val textClassification = new TextClassifier(param)

  textClassification.train()

  class TextClassifier(param: AbstractTextClassificationParams) extends Serializable{

    val gloveDir = s"${param.baseDir}/glove.6B/"
    val textDataDir = s"${param.baseDir}/20_newsgroup/"
    var classNum = -1

    import scala.collection.mutable.{ArrayBuffer, Map => MMap}

    /**
      * Load the pre-trained word2Vec
      * @return A map from word to vector
      */
    def buildWord2Vec(word2Meta: Map[String, WordMeta]): Map[Float, Array[Float]] = {
      println("Indexing word vectors.")
      val preWord2Vec = MMap[Float, Array[Float]]()
      val filename = s"$gloveDir/glove.6B.200d.txt"
      for (line <- Source.fromFile(filename, "ISO-8859-1").getLines) {
        val values = line.split(" ")
        val word = values(0)
        if (word2Meta.contains(word)) {
          val coefs = values.slice(1, values.length).map(_.toFloat)
          preWord2Vec.put(word2Meta(word).index.toFloat, coefs)
        }
      }
      println(s"Found ${preWord2Vec.size} word vectors.")
      preWord2Vec.toMap
    }

    /**
      * Load the pre-trained word2Vec
      * @return A map from word to vector
      */
    def buildWord2VecWithIndex(word2Meta: Map[String, Int]): Map[Float, Array[Float]] = {
      println("Indexing word vectors.")
      val preWord2Vec = MMap[Float, Array[Float]]()
      val filename = s"$gloveDir/glove.6B.200d.txt"
      for (line <- Source.fromFile(filename, "ISO-8859-1").getLines) {
        val values = line.split(" ")
        val word = values(0)
        if (word2Meta.contains(word)) {
          val coefs = values.slice(1, values.length).map(_.toFloat)
          preWord2Vec.put(word2Meta(word).toFloat, coefs)
        }
      }
      println(s"Found ${preWord2Vec.size} word vectors.")
      preWord2Vec.toMap
    }


    /**
      * Load the training data from the given baseDir
      * @return An array of sample
      */
    private def loadRawData(): ArrayBuffer[(String, Float)] = {
      val texts = ArrayBuffer[String]()
      val labels = ArrayBuffer[Float]()
      // category is a string name, label is it's index
      val categoryToLabel = new java.util.HashMap[String, Int]()
      val fs = new File(textDataDir).listFiles()
      val categoryPathList = fs
        .filter(_.isDirectory).toList.sorted

      categoryPathList.foreach { categoryPath =>
        val label_id = categoryToLabel.size() + 1 // one-base index
        categoryToLabel.put(categoryPath.getName(), label_id)
        val textFiles = categoryPath.listFiles()
          .filter(_.isFile).filter(_.getName.forall(Character.isDigit(_))).sorted
        textFiles.foreach { file =>
          val source = Source.fromFile(file, "ISO-8859-1")
          val text = try source.getLines().toList.mkString("\n") finally source.close()
          texts.append(text)
          labels.append(label_id)
        }
      }
      this.classNum = labels.toSet.size
      println(s"Found ${texts.length} texts.")
      println(s"Found ${classNum} classes")
      texts.zip(labels)
    }

    /**
      * Go through the whole data set to gather some meta info for the tokens.
      * Tokens would be discarded if the frequency ranking is less then maxWordsNum
      */
    def analyzeTexts(dataRdd: RDD[(String, Float)])
    : (Map[String, WordMeta], Map[Float, Array[Float]]) = {
      // Remove the top 10 words roughly, you might want to fine tuning this.
      val frequencies = dataRdd.flatMap{case (text: String, label: Float) =>
        toTokens(text)
      }.map(word => (word, 1)).reduceByKey(_ + _)
        .sortBy(- _._2).collect().slice(10, param.maxWordsNum)

      val indexes = Range(1, frequencies.length)
      val word2Meta = frequencies.zip(indexes).map{item =>
        (item._1._1, WordMeta(item._1._2, item._2))}.toMap
      (word2Meta, buildWord2Vec(word2Meta))
    }

    /**
      * Create train and val RDDs from input
      */
    def getData(sc: SparkContext): (Array[RDD[(Array[Array[Float]], Float)]],
      Map[String, WordMeta],
      Map[Float, Array[Float]]) = {

      val sequenceLen = param.maxSequenceLength
      val embeddingDim = param.embeddingDim
      val trainingSplit = param.trainingSplit
      // For large dataset, you might want to get such RDD[(String, Float)] from HDFS
      val dataRdd = sc.parallelize(loadRawData(), param.partitionNum)
      val (word2Meta, word2Vec) = analyzeTexts(dataRdd)
      val word2MetaBC = sc.broadcast(word2Meta)
      val word2VecBC = sc.broadcast(word2Vec)
      val vectorizedRdd = dataRdd
        .map { case (text, label) => (toTokens(text, word2MetaBC.value), label) }
        .map { case (tokens, label) => (shaping(tokens, sequenceLen), label) }
        .map { case (tokens, label) => (vectorization(
          tokens, embeddingDim, word2VecBC.value), label)
        }

      (vectorizedRdd.randomSplit(
        Array(trainingSplit, 1 - trainingSplit)), word2Meta, word2Vec)

    }

    // TODO: Replace SpatialConv and SpatialMaxPolling with 1D implementation
    /**
      * Return a text classification model with the specific num of
      * class
      */
    def buildModel(classNum: Int): Sequential[Float] = {
      val model = Sequential[Float]()

      model.add(TemporalConvolution(param.embeddingDim, 256, 5))
        .add(ReLU())
        .add(TemporalMaxPooling(param.maxSequenceLength - 5 + 1))
        .add(Squeeze(2))
        .add(Linear(256, 128))
        .add(Dropout(0.2))
        .add(ReLU())
        .add(Linear(128, classNum))
        .add(LogSoftMax())
      model
    }


    /**
      * Start to train the text classification model
      */
    def train(): Unit = {
      val conf = Engine.createSparkConf()
        .setAppName("Text classification")
        .set("spark.task.maxFailures", "1")
        .setMaster("local[*]")
      val sc = new SparkContext(conf)
      Engine.init
      val sequenceLen = param.maxSequenceLength
      val embeddingDim = param.embeddingDim
      val trainingSplit = param.trainingSplit

      // For large dataset, you might want to get such RDD[(String, Float)] from HDFS
      val dataRdd = sc.parallelize(loadRawData(), param.partitionNum)
      val (word2Meta, word2Vec) = analyzeTexts(dataRdd)
      val word2MetaBC = sc.broadcast(word2Meta)
      val word2VecBC = sc.broadcast(word2Vec)
      val vectorizedRdd = dataRdd
        .map {case (text, label) => (toTokens(text, word2MetaBC.value), label)}
        .map {case (tokens, label) => (shaping(tokens, sequenceLen), label)}
        .map {case (tokens, label) => (vectorization(
          tokens, embeddingDim, word2VecBC.value), label)}
      val sampleRDD = vectorizedRdd.map {case (input: Array[Array[Float]], label: Float) =>
        Sample(
          featureTensor = Tensor(input.flatten, Array(sequenceLen, embeddingDim)),
          label = label)
      }

      val Array(trainingRDD, valRDD) = sampleRDD.randomSplit(
        Array(trainingSplit, 1 - trainingSplit))

      val optimizer = Optimizer(
        model = buildModel(classNum),
        sampleRDD = trainingRDD,
        criterion = new ClassNLLCriterion[Float](),
        batchSize = param.batchSize
      )

      optimizer
        .setOptimMethod(new Adagrad(learningRate = param.learningRate,
          learningRateDecay = 0.001))
        .setValidation(Trigger.everyEpoch, valRDD, Array(new Top1Accuracy[Float]), param.batchSize)
        .setEndWhen(Trigger.maxEpoch(1))
        .optimize()
      sc.stop()
    }

    /**
      * Train the text classification model with train and val RDDs
      */
    def trainFromData(sc: SparkContext, rdds: Array[RDD[(Array[Array[Float]], Float)]])
    : Module[Float] = {

      // create rdd from input directory
      val trainingRDD = rdds(0).map { case (input: Array[Array[Float]], label: Float) =>
        Sample(
          featureTensor = Tensor(input.flatten, Array(param.maxSequenceLength, param.embeddingDim))
            .transpose(1, 2).contiguous(),
          label = label)
      }

      val valRDD = rdds(1).map { case (input: Array[Array[Float]], label: Float) =>
        Sample(
          featureTensor = Tensor(input.flatten, Array(param.maxSequenceLength, param.embeddingDim))
            .transpose(1, 2).contiguous(),
          label = label)
      }

      // train
      val optimizer = Optimizer(
        model = buildModel(classNum),
        sampleRDD = trainingRDD,
        criterion = new ClassNLLCriterion[Float](),
        batchSize = param.batchSize
      )

      optimizer
        .setOptimMethod(new Adagrad(learningRate = param.learningRate, learningRateDecay = 0.0002))
        .setValidation(Trigger.everyEpoch, valRDD, Array(new Top1Accuracy[Float]), param.batchSize)
        .setEndWhen(Trigger.maxEpoch(1))
        .optimize()
    }
  }


//  sc.stop()
}
