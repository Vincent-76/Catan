package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

import scala.util.Try

/**
 * @author Vincent76;
 */
case object PlayerTradeCommand
  extends CommandAction( "ptrade", List( "giveResources", "getResources" ), "Trade resources with other players." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = {
    val parts = commandInput.input.split( "\\s+", 2 )( 1 ).split( "\\s*-\\s*" )
    (controller.action( _.setPlayerTradeState( TUI.parseResources( parts( 0 ) ), TUI.parseResources( parts( 1 ) ) ) ), Nil)
  }

  override protected def getInputPattern:String = {
    "(?=" + TUI.regexIgnoreCase( command ) + ")((" + TUI.regexIgnoreCase( command ) + "|,)" +
      TUI.resourcePattern + ")+(?=-)((-|,)" + TUI.resourcePattern + ")+"
  }

  override def getSyntax:String = "[" + command + " " +
    parameter.map( p => "<" + p + ">" ).mkString( " - " ) +
    "] with resources: [" + TUI.resourcePatternInfo + ", ...]"
}