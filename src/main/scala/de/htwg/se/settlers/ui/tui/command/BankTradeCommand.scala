package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.{ Resources, State }
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object BankTradeCommand
  extends CommandAction( "btrade", List( "giveResource", "getResource" ), "Trade resources with the bank." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    state.bankTrade( TUI.parseResource( parts( 0 ) ).get, TUI.parseResource( parts( 1 ) ).get )
  }

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+" + TUI.resourcePattern + "-" + TUI.resourcePattern
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resource: [<" + Resources.get.map( _.s ).mkString( "|" ) + "> <amount>]"
}
