package org.ditw.book.tinyWebS

trait Controller {
  def handleRequest(req:HttpRequest):HttpResponse
}

class FunctionController(val view:View, doRequest:HttpRequest => Map[String, List[String]]) extends Controller {
  override def handleRequest(req: HttpRequest): HttpResponse = {

    try {

      val model = doRequest(req)
      HttpResponse(200, view.render(model))
    }
    catch {
      case re:RenderingExceptionS => {
        HttpResponse(500, "RenderingExceptionS")
      }
      case e:Throwable => {
        HttpResponse(500, "Throwable")
      }
    }
  }
}
