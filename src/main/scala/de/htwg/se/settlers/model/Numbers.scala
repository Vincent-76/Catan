package de.htwg.se.settlers.model

import com.sun.javaws.exceptions.InvalidArgumentException

/**
 * @author Vincent76;
 */

object Numbers {
  val maxFrequency = 5
  val maxSum = 12

  def of( n:Int ):Option[Number] = {
    n match {
      case 2 => Some( Two )
      case 3 => Some( Three )
      case 4 => Some( Four )
      case 5 => Some( Five )
      case 6 => Some( Six )
      case 7 => Some( Seven )
      case 8 => Some( Eight )
      case 9 => Some( Nine )
      case 10 => Some( Ten )
      case 11 => Some( Eleven )
      case 12 => Some( Twelve )
      case _ => None
    }
  }
}

sealed abstract class Number( val frequency:Int ) {
  def value:Int

  override def toString:String = {
    if( value < 10 )
      "0" + value
    else
      value.toString
  }
}

case object Two extends Number( 1 ) {
  override def value:Int = 2
}

case object Three extends Number( 2 ) {
  override def value:Int = 3
}

case object Four extends Number( 3 ) {
  override def value:Int = 4
}

case object Five extends Number( 4 ) {
  override def value:Int = 5
}

case object Six extends Number( 5 ) {
  override def value:Int = 6
}

case object Seven extends Number( 0 ) {
  override def value:Int = 7
}

case object Eight extends Number( 5 ) {
  override def value:Int = 8
}

case object Nine extends Number( 4 ) {
  override def value:Int = 9
}

case object Ten extends Number( 3 ) {
  override def value:Int = 10
}

case object Eleven extends Number( 2 ) {
  override def value:Int = 11
}

case object Twelve extends Number( 1 ) {
  override def value:Int = 12
}
