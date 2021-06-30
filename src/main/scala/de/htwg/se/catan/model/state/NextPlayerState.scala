package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State, StateImpl }
import de.htwg.se.catan.model.commands.ChangeStateCommand
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object NextPlayerState extends StateImpl( "NextPlayerState" ) {
  def fromXML( node:Node ):NextPlayerState = NextPlayerState()

  def fromJson( json:JsValue ):NextPlayerState = NextPlayerState()
}

case class NextPlayerState( ) extends State {

  def toXML:Node = <NextPlayerState />.copy( label = NextPlayerState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NextPlayerState.name )
  )

  override def startTurn( ):Option[Command] = Some(
    ChangeStateCommand( this, DiceState() )
  )

  //override def toString:String = getClass.getSimpleName
}
