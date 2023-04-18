package de.htwg.se.catan.model

import play.api.libs.json._

/**
 * @author Vincent76;
 */

object DiceValue:
  val maxFrequency = 5
  val maxSum = 12

  given diceValueWrites:Writes[DiceValue] = ( o:DiceValue ) => Json.toJson( o.value )
  given diceValueReads:Reads[DiceValue] = ( json:JsValue ) => JsSuccess( of( json.as[Int] ).get )

  def of( n:Int ):Option[DiceValue] = DiceValue.values.find( _.value == n )

enum DiceValue( val value:Int, val frequency:Int ):

  override def toString:String = {
    if( value < 10 )
      "0" + value
    else
      value.toString
  }

  case Two extends DiceValue( 2, 1 )
  case Three extends DiceValue( 3, 2 )
  case Four extends DiceValue( 4, 3 )
  case Five extends DiceValue( 5, 4 )
  case Six extends DiceValue( 6, 5 )
  case Seven extends DiceValue( 7, 0 )
  case Eight extends DiceValue( 8, 5 )
  case Nine extends DiceValue( 9, 4 )
  case Ten extends DiceValue( 10, 3 )
  case Eleven extends DiceValue( 11, 2 )
  case Twelve extends DiceValue( 12, 1 )
