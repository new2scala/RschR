package org.ditw.book.tinyWebS

case class HttpRequest(val body:String, val path:String, val headers:Map[String,String]) {

}
