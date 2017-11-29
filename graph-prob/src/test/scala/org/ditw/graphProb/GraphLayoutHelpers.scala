package org.ditw.graphProb

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-11-29.
  */
object GraphLayoutHelpers extends App {

  private def _calcCoords(vertexCount:Int):(Int, Array[(Int, Int)]) = {
    var found = false

    var steps = 1
    while (vertexCount > steps*steps) {
      steps = steps+1
    }

    val max = steps-1
    val r = (0 to max*2).flatMap { sum =>
      (0 to sum).flatMap(x => if (x <= max && (sum-x) <= max) Option((sum-x) -> x) else None)
    }

    steps -> r.take(vertexCount).toArray
    //val span = canvasSize/(steps+2)
  }

  def calcCoords(vertexCount:Int, canvasSize:Int):Array[(Int, Int)] = {
    val (steps, coords) = _calcCoords(vertexCount)

    val span = canvasSize/(steps+1)
    coords.map(p => (p._1+1)*span -> (p._2+1)*span)
  }

  var t = calcCoords(5, 600)
  println(t)
//
//  t = calcCoords(4)
//  println(t)
//

//
//  t = calcCoords(10)
//  println(t)
}
