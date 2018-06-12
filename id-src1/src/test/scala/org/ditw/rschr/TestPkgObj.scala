package org.ditw.rschr

object TestPkgObj extends App {
  import pkgObj._

  println(apiEcho(constParam.toString))
}
