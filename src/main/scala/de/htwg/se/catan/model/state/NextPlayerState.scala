package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.ChangeStateCommand

import scala.xml.Node

/**
 * @author Vincent76;
 */

object NextPlayerState {
  def fromXML( node:Node ):NextPlayerState = NextPlayerState()
}

case class NextPlayerState( ) extends State {

  def toXML:Node = <NextPlayerState />

  override def startTurn( ):Option[Command] = Some(
    ChangeStateCommand( this, DiceState() )
  )

  //override def toString:String = getClass.getSimpleName
}
