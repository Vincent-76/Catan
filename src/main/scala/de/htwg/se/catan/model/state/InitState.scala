package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.commands.InitGameCommand
import de.htwg.se.catan.model.{ Command, State }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitState {
  def fromXML( node:Node ):InitState = InitState()
}

case class InitState( ) extends State {

  def toXML:Node = <InitState />

  override def initGame( ):Option[Command] = Some(
    InitGameCommand()
  )

  //override def toString:String = getClass.getSimpleName
}
