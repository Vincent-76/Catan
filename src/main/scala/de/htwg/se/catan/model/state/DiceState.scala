package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ DevelopmentCard, _ }
import de.htwg.se.catan.model.commands.{ RollDicesCommand, UseDevCardCommand }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DiceState extends StateImpl( "DiceState" ) {
  def fromXML( node:Node ):DiceState = DiceState()

  def fromJson( json:JsValue ):DiceState = DiceState()
}

case class DiceState() extends State {

  def toXML:Node = <DiceState />.copy( label = DiceState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DiceState.name )
  )

  override def useDevCard( devCard:DevelopmentCard ):Option[Command] = Some(
    UseDevCardCommand( devCard, this )
  )


  override def rollTheDices( ):Option[Command] = Some(
    RollDicesCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
