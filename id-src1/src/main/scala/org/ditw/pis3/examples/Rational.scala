package org.ditw.pis3.examples

import Rational._
class Rational(n:Int, d:Int) {

  require(d != 0)

  def this(i:Int) = this(i, 1)

  private val _gcd = gcd(n, d)
  private val _minus = {
    (n < 0 && d > 0) || (n > 0 && d < 0)
  }
  val numer:Int = {
    val r = math.abs(n)/_gcd
    if (_minus) -r
    else r
  }
  val denom:Int = {
    if (n == 0) 1 else math.abs(d)/_gcd
  }

  def +(that:Rational):Rational = {
    val n1 = numer*that.denom + denom*that.numer
    val d1 = denom*that.denom
    new Rational(n1, d1)
  }

  override def hashCode(): Int = (n << 16) + d

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case r:Rational => r.numer == numer && r.denom == denom
      case _ => false
    }
  }
}

object Rational {
  private[examples] def gcd(n1:Int, n2:Int):Int = {
    val (abs1, abs2) = math.abs(n1) -> math.abs(n2)
    var (big, small) = if (abs1 > abs2) abs1 -> abs2 else abs2 -> abs1

    if (small == 0)
      1
    else {
      while (big % small != 0) {
        val t = small
        small = big % small
        big = t
      }
      small
    }

  }
}