package org.ditw.rschr.utils

import org.apache.spark.graphx.Graph

/**
  * Created by dev on 2018-02-20.
  */
object GraphUtils {

  def traceGraph[VD, ED](graph:Graph[VD, ED]):String = {
    graph.triplets.map(
      triplet => s"SRC[${triplet.srcAttr}] - E[${triplet.attr}] - DST[${triplet.dstAttr}]"
    ).collect.mkString("\n")
  }
}
