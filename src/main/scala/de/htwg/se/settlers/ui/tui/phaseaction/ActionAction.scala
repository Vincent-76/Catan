package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.command.{ BankTradeCommand, BuildCommand, BuyDevCommand, CommandAction, PlayerTradeCommand, UseDevCommand }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class ActionAction( controller:Controller ) extends PhaseAction( controller ) {

  private val availableCommands:List[CommandAction] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCommand,
    UseDevCommand
  )

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.onTurn ) ) )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( "Available commands:" )
    val descLength = availableCommands.map( _.desc.length ).max
    availableCommands.foreach( c => TUI.outln( c.desc.toLength( descLength ) + "   " + c.getSyntax ) )
    Some( "Type command, or [end] to end your turn" )
  }

  override def inputPattern:Option[String] = {
    Some( "(" + TUI.regexIgnoreCase( "end" ) + "|" + availableCommands.map( _.inputPattern ).mkString( "|" ) + ")" )
  }

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    availableCommands.find( c => c.command.toLowerCase() == commandInput.command.get.toLowerCase() ) match {
      case Some( c ) => c.action( commandInput, controller )
      case _ => controller.setNextPlayerPhase()
    }
  }


}
