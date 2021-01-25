package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ State, StructurePlacement }
import de.htwg.se.settlers.model.commands.BuildCommand

/**
 * @author Vincent76;
 */
case class BuildState( controller:Controller, structure:StructurePlacement ) extends State( controller ) {

  override def build( id:Int ):Unit = controller.action(
    BuildCommand( id, this )
  )
}
