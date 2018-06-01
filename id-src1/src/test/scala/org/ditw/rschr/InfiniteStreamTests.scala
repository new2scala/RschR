package org.ditw.rschr

import scala.util.Random

object InfiniteStreamTests extends App {

  val gen = new Random()

  val rands = Stream.continually(gen.nextInt())

  rands.take(10).foreach { r =>
    Thread.sleep(500)
    println(r)
  }

}
