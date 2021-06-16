package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{Command, State}
import de.htwg.se.catan.model.commands.BuildInitRoadCommand

/**
 * @author Vincent76;
 */
case class BuildInitRoadState( settlementVID:Int ) extends State {

  override def buildInitRoad( eID:Int ):Option[Command] = Some(
    BuildInitRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": SettlementVID[" + settlementVID + "]"
}
