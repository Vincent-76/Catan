package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case class DevYearOfPlentyAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.onTurn ) ) )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( "You can specify 2 resources to get from the bank." )
    Some( "Type [" + TUI.resourcePatternInfo + ", ...] to specify resources" )
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Option[Throwable] =
    controller.yearOfPlentyAction( TUI.parseResources( commandInput.input ) ) match {
      case Success( resources ) =>
        println()
        TUI.outln( "You've got " + resources.display + " from the bank." )
        Option.empty
      case Failure( e ) => Some( e )
    }
}
