package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.BuildInitSettlementCommand

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildInitSettlementState {
  def fromXML( node:Node ):BuildInitSettlementState = BuildInitSettlementState()
}

case class BuildInitSettlementState() extends State {

  def toXML:Node = <BuildInitSettlementState />

  override def buildInitSettlement( vID:Int ):Option[Command] = Some(
    BuildInitSettlementCommand( vID, this )
  )

  //override def toString:String = getClass.getSimpleName
}
