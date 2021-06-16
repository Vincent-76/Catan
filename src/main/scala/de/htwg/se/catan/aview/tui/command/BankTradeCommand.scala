package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Resources
import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object BankTradeCommand
  extends CommandAction( "btrade", List( "giveResource", "getResource" ), "Trade resources with the bank." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    controller.bankTrade( TUI.parseResources( parts( 0 ) ), TUI.parseResources( parts( 1 ) ) )
  }

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+" + TUI.resourcePattern + "-" + TUI.resourcePattern
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resource: [<" + Resources.get.map( _.title ).mkString( "|" ) + "> <amount>]"
}
