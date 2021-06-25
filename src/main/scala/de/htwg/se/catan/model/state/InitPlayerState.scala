package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.commands.{ AddPlayerCommand, SetInitBeginnerStateCommand }
import de.htwg.se.catan.model.{ Command, PlayerColor, State, StateImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitPlayerState extends StateImpl( "InitPlayerState" ) {
  def fromXML( node:Node ):InitPlayerState = InitPlayerState()

  def fromJson( json:JsValue ):State = InitPlayerState()
}

case class InitPlayerState( ) extends State {

  def toXML:Node = <InitPlayerState />.copy( label = InitPlayerState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InitPlayerState.name )
  )

  override def addPlayer( playerColor:PlayerColor, name:String ):Option[Command] = Some(
    AddPlayerCommand( playerColor, name, this )
  )

  override def setInitBeginnerState( ):Option[Command] = Some(
    SetInitBeginnerStateCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
