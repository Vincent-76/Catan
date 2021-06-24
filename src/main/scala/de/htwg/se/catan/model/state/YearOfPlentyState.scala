package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.YearOfPlentyCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode

import scala.xml.Node

/**
 * @author Vincent76;
 */

object YearOfPlentyState {
  def fromXML( node:Node ):YearOfPlentyState = YearOfPlentyState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )
}

case class YearOfPlentyState( nextState:State ) extends State {

  def toXML:Node = <YearOfPlentyState>
    <nextState>{ nextState.toXML }</nextState>
  </YearOfPlentyState>

  override def yearOfPlentyAction( resources:ResourceCards ):Option[Command] = Some(
    YearOfPlentyCommand( resources, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
