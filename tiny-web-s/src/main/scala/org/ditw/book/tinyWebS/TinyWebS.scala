package org.ditw.book.tinyWebS

class TinyWebS(controllers: Map[String, Controller], filters:List[HttpRequest => HttpRequest]) {

  def handleRequest(req:HttpRequest):Option[HttpResponse] = {
    val composedFilter = filters.reverse.reduceLeft((c, n) => c compose n)

    val filteredReq = composedFilter(req)
    val controller = controllers.get(filteredReq.path)
    controller.map(_.handleRequest(filteredReq))
  }

}
