package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.command._
import com.aimit.htwg.catan.view.tui._
import com.aimit.htwg.catan.util._

/**
 * @author Vincent76;
 */
case class ActionTUIState( controller:Controller ) extends TUIState {

  private val availableCommands:List[CommandAction] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCommand,
    UseDevCommand
  )

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String = {
    TUI.outln( "Available commands:" )
    val descLength = availableCommands.map( _.desc.length ).max
    availableCommands.foreach( c => TUI.outln( c.desc.toLength( descLength ) + "   " + c.getSyntax ) )
    "Type command, or [end] to end your turn"
  }

  override def inputPattern:Option[String] = {
    Some( "(" + TUI.regexIgnoreCase( "end" ) + "|" + availableCommands.map( _.inputPattern ).mkString( "|" ) + ")" )
  }

  override def action( commandInput:CommandInput ):Unit = availableCommands.find( c => c.command ^= commandInput.command.get ) match {
    case Some( c ) => c.action( commandInput, controller )
    case _ if commandInput.input ^= "end" => controller.endTurn()
    case _ =>
  }
}
