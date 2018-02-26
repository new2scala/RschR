package org.ditw.rschr

/**
  * Created by dev on 2018-02-26.
  */
object TestUtils {

  private val tolerance = 1e-10

  def doubleEquals(v1:Double, v2:Double):Boolean = {
    math.abs(v1-v2) <= tolerance
  }
}
