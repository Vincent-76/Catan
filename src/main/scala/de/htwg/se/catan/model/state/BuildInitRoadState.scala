package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.BuildInitRoadCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildInitRoadState {
  def fromXML( node:Node ):BuildInitRoadState = BuildInitRoadState(
    settlementVID = ( node \ "@settlementVID" ).content.toInt
  )
}

case class BuildInitRoadState( settlementVID:Int ) extends State {

  def toXML:Node = <BuildInitRoadState settlementVID={ settlementVID.toString } />

  override def buildInitRoad( eID:Int ):Option[Command] = Some(
    BuildInitRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": SettlementVID[" + settlementVID + "]"
}
