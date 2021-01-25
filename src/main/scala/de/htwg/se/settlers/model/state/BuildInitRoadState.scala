package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.BuildInitRoadCommand

/**
 * @author Vincent76;
 */
case class BuildInitRoadState( controller:Controller, settlementVID:Int ) extends State( controller ) {

  override def buildInitRoad( eID:Int ):Unit = controller.action(
    BuildInitRoadCommand( eID, this )
  )

}
