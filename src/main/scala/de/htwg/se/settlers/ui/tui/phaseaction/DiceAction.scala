package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case class DiceAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.onTurn ) ) )
  }

  override def actionInfo:Option[String] = Some( "Press Enter to roll the dices" )

  override def action( commandInput:CommandInput ):Option[Throwable] = controller.rollDices() match {
    case Success( dices ) =>
      TUI.outln( dices._1 + " + " + dices._2 + " = " + ( dices._1 + dices._2 ) )
      TUI.awaitKey()
      Option.empty
    case Failure( e ) => Some( e )
  }
}
