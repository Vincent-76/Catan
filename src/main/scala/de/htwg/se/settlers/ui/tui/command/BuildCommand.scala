package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.StructurePlacement
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object BuildCommand extends CommandAction( "build", List( StructurePlacement.get.map( _.s ).mkString( "|" ) ), "Build a new structure." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Option[Throwable] = {
    val placement = StructurePlacement.of( commandInput.args( 0 ) )
    controller.setBuildPhase( placement.get )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + "\\s+(" + StructurePlacement.get.map( p => TUI.regexIgnoreCase( p.s ) ).mkString( "|" ) + ")"
}
