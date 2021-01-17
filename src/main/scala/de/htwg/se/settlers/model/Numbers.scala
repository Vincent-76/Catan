package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

object Numbers {
  val maxFrequency = 5
  val maxSum = 12

  def of( n:Int ):Number = {
    n match {
      case 2 => Two
      case 3 => Three
      case 4 => Four
      case 5 => Five
      case 6 => Six
      case 7 => Seven
      case 8 => Eight
      case 9 => Nine
      case 10 => Ten
      case 11 => Eleven
      case 12 => Twelve
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
