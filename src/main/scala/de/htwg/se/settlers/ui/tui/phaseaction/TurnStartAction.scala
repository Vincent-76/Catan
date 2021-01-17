package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.command.UseDevCommand
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

/**
 * @author Vincent76;
 */
case class TurnStartAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.onTurn ) ) )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( "Available commands:" )
    TUI.outln( UseDevCommand.desc + "   " + UseDevCommand.getSyntax )
    Some( "Type command, or [roll] to proceed to roll the dices" )
  }

  override def inputPattern:Option[String] =
    Some( "(" + TUI.regexIgnoreCase( "roll" ) + "|" + UseDevCommand.inputPattern + ")" )

  override def action( commandInput:CommandInput ):Option[Throwable] = commandInput.input.toLowerCase match {
    case "roll" => controller.setDicePhase()
    case _ => UseDevCommand.action( commandInput, controller )
  }
}
