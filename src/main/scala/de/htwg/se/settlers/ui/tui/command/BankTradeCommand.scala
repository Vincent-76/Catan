package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Resources
import de.htwg.se.settlers.ui.tui.command.PlayerTradeCommand.command
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case object BankTradeCommand extends CommandAction( "btrade", List( "giveResource", "getResource" ), "Trade resources with the bank." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Option[Throwable] = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    controller.bankTrade( TUI.parseResource( parts( 0 ) ).get, TUI.parseResource( parts( 1 ) ).get ) match {
      case Success( r ) =>
        println()
        TUI.outln( "You traded " + r._1._2 + " " + r._1._1.s + " for " + r._2._2 + " " + r._2._1.s + "." )
        TUI.awaitKey()
        Option.empty
      case Failure( e ) => Some( e )
    }
  }

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+" + TUI.resourcePattern + "-" + TUI.resourcePattern
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resource: [<" + Resources.get.map( _.s ).mkString( "|" ) + "> <amount>]"
}
