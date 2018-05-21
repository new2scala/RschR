package org.ditw.book.tinyWebS

import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test

class TinyWebSTest extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  private def viewRenderer(model:Map[String, List[String]]):String = {
    val names = model("greetings")
    names.map(n => s"<h2>Greetings $n</h2>\n")
      .mkString
  }

  private def doReq(req:HttpRequest):Map[String, List[String]] = {
    val ns = req.body.split(",")
    Map("greetings" -> ns.toList)
  }

  private def logFilter(req:HttpRequest):HttpRequest = {
    s"Logging: ${req.path}"
    req
  }

  @Test
  def test1():Unit = {
    val tws = new TinyWebS(
      Map("greetings" -> new FunctionController(new FunctionView(viewRenderer), doReq)),
      List(logFilter)
    )

    val resp = tws.handleRequest(
      HttpRequest("Amy,Bob, Charlie", "greetings", Map())
    )
    println(resp)
  }

}
