package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.command._
import de.htwg.se.settlers.aview.tui._
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class ActionTUIState( controller: Controller ) extends TUIState {

  private val availableCommands:List[CommandAction] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCommand,
    UseDevCommand
  )

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( "Available commands:" )
    val descLength = availableCommands.map( _.desc.length ).max
    availableCommands.foreach( c => TUI.outln( c.desc.toLength( descLength ) + "   " + c.getSyntax ) )
    "Type command, or [end] to end your turn"
  }

  override def inputPattern:Option[String] = {
    Some( "(" + TUI.regexIgnoreCase( "end" ) + "|" + availableCommands.map( _.inputPattern ).mkString( "|" ) + ")" )
  }

  override def action( commandInput:CommandInput ):Unit = availableCommands.find( c => c.command =^ commandInput.command.get ) match {
    case Some( c ) => c.action( commandInput, controller )
    case _ if commandInput.input =^ "end" => controller.game.state.endTurn()
    case _ =>
  }
}
