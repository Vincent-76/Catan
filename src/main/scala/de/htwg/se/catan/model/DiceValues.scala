package de.htwg.se.catan.model

/**
 * @author Vincent76;
 */

object DiceValues {
  val maxFrequency = 5
  val maxSum = 12

  val all:List[DiceValue] = List(
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

  def of( n:Int ):Option[DiceValue] = {
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

sealed abstract class DiceValue( val value:Int, val frequency:Int ) {

  override def toString:String = {
    if( value < 10 )
      "0" + value
    else
      value.toString
  }
}

case object Two extends DiceValue( 2, 1 )

case object Three extends DiceValue( 3, 2 )

case object Four extends DiceValue( 4, 3 )

case object Five extends DiceValue( 5, 4 )

case object Six extends DiceValue( 6, 5 )

case object Seven extends DiceValue( 7, 0 )

case object Eight extends DiceValue( 8, 5 )

case object Nine extends DiceValue( 9, 4 )

case object Ten extends DiceValue( 10, 3 )

case object Eleven extends DiceValue( 11, 2 )

case object Twelve extends DiceValue( 12, 1 )
