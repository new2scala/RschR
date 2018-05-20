package org.ditw.book.tinyWebS

trait View {
  def render(model: Map[String, List[String]]):String
}

class FunctionView(viewRenderer: Map[String, List[String]] => String) extends View {
  def render(model: Map[String, List[String]]):String = {
    try {
      viewRenderer(model)
    }
    catch {
      case e:Exception => throw new RenderingExceptionS(e)
    }
  }
}