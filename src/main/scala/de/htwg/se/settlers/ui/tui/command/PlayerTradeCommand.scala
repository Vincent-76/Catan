package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object PlayerTradeCommand
  extends CommandAction( "ptrade", List( "giveResources", "getResources" ), "Trade resources with other players." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    state.setPlayerTradeState( TUI.parseResources( parts( 0 ) ), TUI.parseResources( parts( 1 ) ) )
  }

  override protected def getInputPattern:String = {
    "(?=" + TUI.regexIgnoreCase( command ) + ")((" + TUI.regexIgnoreCase( command ) + "|,)" +
      TUI.resourcePattern + ")+(?=-)((-|,)" + TUI.resourcePattern + ")+"
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resources: [" + TUI.resourcePatternInfo + ", ...]"
}