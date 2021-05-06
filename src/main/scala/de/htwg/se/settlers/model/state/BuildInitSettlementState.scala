package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{Command, State}
import de.htwg.se.settlers.model.commands.BuildInitSettlementCommand

/**
 * @author Vincent76;
 */
case class BuildInitSettlementState() extends State {

  override def buildInitSettlement( vID:Int ):Option[Command] = Some(
    BuildInitSettlementCommand( vID, this )
  )

  //override def toString:String = getClass.getSimpleName
}
