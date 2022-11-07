package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Info, Resource }
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

import scala.util.Try

/**
 * @author Vincent76;
 */
case object BankTradeCommand
  extends CommandAction( "btrade", List( "giveResource", "getResource" ), "Trade resources with the bank." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    (controller.action( _.bankTrade( TUI.parseResources( parts( 0 ) ), TUI.parseResources( parts( 1 ) ) ) ), Nil)
  }

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+" + TUI.resourcePattern + "-" + TUI.resourcePattern
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resource: [<" + Resource.impls.map( _.title ).mkString( "|" ) + "> <amount>]"
}
