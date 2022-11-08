package com.aimit.htwg.catan.model

/**
 * @author Vincent76;
 */

object DiceValue extends SerialComponent[Int, DiceValue] {
  val maxFrequency = 5
  val maxSum = 12

  /*implicit val diceValueWrites:Writes[DiceValue] = ( o:DiceValue ) => Json.toJson( o.value )
  implicit val diceValueReads:Reads[DiceValue] = ( json:JsValue ) => JsSuccess( of( json.as[Int] ).get )*/

  Two.init()
  Three.init()
  Four.init()
  Five.init()
  Six.init()
  Seven.init()
  Eight.init()
  Nine.init()
  Ten.init()
  Eleven.init()
  Twelve.init()
}

sealed abstract class DiceValue( val value:Int, val frequency:Int ) extends SerialComponentImpl( value ) {
  override def init():Unit = DiceValue.addImpl( this )

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
