package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.commands.{ AddPlayerCommand, SetInitBeginnerStateCommand }
import de.htwg.se.catan.model.{ Command, PlayerColor, State }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitPlayerState {
  def fromXML( node:Node ):InitPlayerState = InitPlayerState()
}

case class InitPlayerState( ) extends State {

  def toXML:Node = <InitPlayerState />

  override def addPlayer( playerColor:PlayerColor, name:String ):Option[Command] = Some(
    AddPlayerCommand( playerColor, name, this )
  )

  override def setInitBeginnerState( ):Option[Command] = Some(
    SetInitBeginnerStateCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
