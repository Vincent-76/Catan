package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.BuildInitSettlementCommand

/**
 * @author Vincent76;
 */
abstract class BuildInitSettlementState( controller:Controller ) extends State( controller ) {

  override def buildInitSettlement( vID:Int ):Unit = controller.action(
    BuildInitSettlementCommand( vID, this )
  )
}
