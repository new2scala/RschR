package org.ditw.texts

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object TokenProcessors extends Serializable {

  private val EmptyTagSet = Set[String]()
  case class Tkn(text:String, s:Int, e:Int, tags:Set[String] = EmptyTagSet, locked:Boolean = false, decoded:Option[String] = None) {
    def asDecoded(d:String):Tkn = copy(decoded = Option(d))
    def decodedOrText:String = decoded.getOrElse(text)
  }

  private def textTkn(txt:String, s:Int, e:Int):Tkn = Tkn(txt, s, e)

  def textTkn(txt:String):Tkn = Tkn(txt, 0, txt.length)

  trait Tkzr extends Serializable {
    def run(tkns:IndexedSeq[Tkn]):IndexedSeq[Tkn] = tkns
  }

  private val SpaceSplitPattern = """\s+""".r
  trait SpaceSepTkzr extends Tkzr {
    abstract override def run(tkns: IndexedSeq[Tkn]): IndexedSeq[Tkn] = {
      tkns.flatMap { tkn =>
        if (!tkn.locked) {
          val txts = SpaceSplitPattern.split(tkn.text).filter(!_.isEmpty)
          var idx = 0
          txts.map { txt =>
            val s = tkn.text.indexOf(txt, idx)
            idx = s+txt.length
            textTkn(txt, tkn.s+s, tkn.s+idx)
          }
        }
        else IndexedSeq(tkn)
      }
    }
  }

  private def splitByRegexMatches(regex:Regex, tkn:Tkn):IndexedSeq[Tkn] = {
    val matches = regex.findAllMatchIn(tkn.text)
    if (matches.nonEmpty) {
      val r = ListBuffer[Tkn]()
      var idx = 0
      matches.foreach { m =>
        if (m.start > idx) {
          val pre = tkn.text.substring(idx, m.start)
          r += textTkn(pre, tkn.s+idx, tkn.s+m.start)
        }
        val mtxt = tkn.text.substring(m.start, m.end)
        r += textTkn(mtxt, tkn.s+m.start, tkn.s+m.end)
        idx = m.end
      }
      if (idx < tkn.text.length) {
        val post = tkn.text.substring(idx)
        r += textTkn(post, tkn.s+idx, tkn.e)
      }
      r.toIndexedSeq
    }
    else IndexedSeq(tkn)
  }

  private val HtmlCodedCharPattern = """&#\d+;""".r
  private def decodeHtmlInToken(tkn:Tkn):Tkn = {
    val matches = HtmlCodedCharPattern.findAllMatchIn(tkn.text)
    if (matches.nonEmpty) {
      val r = ListBuffer[String]()
      var idx = 0
      matches.foreach { m =>
        if (m.start > idx) {
          val pre = tkn.text.substring(idx, m.start)
          r += pre
        }
        val mtxt = tkn.text.substring(m.start, m.end)
        val decoded = mtxt.substring(2, mtxt.length-1).toInt.toChar
        r += decoded.toString
        idx = m.end
      }
      if (idx < tkn.text.length) {
        val post = tkn.text.substring(idx)
        r += post
      }
      tkn.asDecoded(r.mkString)
    }
    else tkn
  }

  class TkzrSplitByRegex(val regex:Regex) extends Tkzr with SpaceSepTkzr {
    override def run(tkns: IndexedSeq[Tkn]): IndexedSeq[Tkn] = {
      super.run(tkns).map(decodeHtmlInToken)
    }
  }
  val TkzrHtmlDecoder = new TkzrSplitByRegex(HtmlCodedCharPattern)
}
