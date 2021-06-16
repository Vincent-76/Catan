package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput, TUI }
import de.htwg.se.catan.model
import de.htwg.se.catan.model.StructurePlacement
import de.htwg.se.catan.util.RichIterable

/**
 * @author Vincent76;
 */
case object BuildCommand
  extends CommandAction( "build", List( StructurePlacement.all.map( _.title ).mkString( "|" ) ), "Build a new structure." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = {
    val placement = controller.game.availablePlacements.withType[model.StructurePlacement].find( _.title.toLowerCase == commandInput.args( 0 ).toLowerCase )
    controller.setBuildState( placement.get )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) +
    "\\s+(" + StructurePlacement.all.map( p => TUI.regexIgnoreCase( p.title ) ).mkString( "|" ) + ")"
}
