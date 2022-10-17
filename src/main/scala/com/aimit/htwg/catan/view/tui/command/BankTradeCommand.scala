package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Resource
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

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
    "] with resource: [<" + Resource.impls.map( _.title ).mkString( "|" ) + "> <amount>]"
}
