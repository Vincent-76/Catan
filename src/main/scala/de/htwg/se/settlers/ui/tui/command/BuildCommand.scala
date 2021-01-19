package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.{ State, StructurePlacement }
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object BuildCommand
  extends CommandAction( "build", List( StructurePlacement.get.map( _.s ).mkString( "|" ) ), "Build a new structure." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = {
    val placement = StructurePlacement.of( commandInput.args( 0 ) )
    state.setBuildState( placement.get )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) +
    "\\s+(" + StructurePlacement.get.map( p => TUI.regexIgnoreCase( p.s ) ).mkString( "|" ) + ")"
}
