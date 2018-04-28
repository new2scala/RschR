package org.ditw.rschr

import com.intel.analytics.bigdl.nn._
import com.intel.analytics.bigdl.utils.Engine
import org.apache.spark.SparkContext

/**
  * Created by dev on 2018-04-29.
  */
object TestBigDl extends App {

  val conf = Engine.createSparkConf()
    .setAppName("Train Lenet on MNIST")
    .set("spark.task.maxFailures", "1")
    .setMaster("local[*]")

  val sc = new SparkContext(conf)

  Engine.init

  val model = Sequential[Double]()
  model.add(Reshape(Array(1, 28, 28)))
    .add(SpatialConvolution(1, 6, 5, 5))
    .add(Tanh())
    .add(SpatialMaxPooling(2, 2, 2, 2))
    .add(Tanh())
    .add(SpatialConvolution(6, 12, 5, 5))
    .add(SpatialMaxPooling(2, 2, 2, 2))
    .add(Reshape(Array(12 * 4 * 4)))
    .add(Linear(12 * 4 * 4, 100))
    .add(Tanh())
    .add(Linear(100, 10))
    .add(LogSoftMax())

  sc.stop()

}
