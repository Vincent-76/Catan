package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

object Numbers {
  val maxFrequency = 5
  val maxSum = 12

  def all:List[Number] = List(
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Eleven,
    Twelve
  )

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

sealed abstract class Number( val value:Int, val frequency:Int ) {

  override def toString:String = {
    if( value < 10 )
      "0" + value
    else
      value.toString
  }
}

case object Two extends Number( 2, 1 )

case object Three extends Number( 3, 2 )

case object Four extends Number( 4, 3 )

case object Five extends Number( 5, 4 )

case object Six extends Number( 6, 5 )

case object Seven extends Number( 7, 0 )

case object Eight extends Number( 8, 5 )

case object Nine extends Number( 9, 4 )

case object Ten extends Number( 10, 3 )

case object Eleven extends Number( 11, 2 )

case object Twelve extends Number( 12, 1 )
