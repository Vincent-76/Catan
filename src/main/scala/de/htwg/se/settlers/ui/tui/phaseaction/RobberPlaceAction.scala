package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Robber, RobberPlacePhase }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case class RobberPlaceAction( robberPlacePhase:RobberPlacePhase, controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller, Robber, controller.onTurn )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.onTurn ) ) )
  }

  override def actionInfo:Option[String] = Some( "Select hex [<id>] for the robber" )

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    controller.placeRobber( commandInput.input.toInt ) match {
      case Success( stolen ) =>
        if( stolen.isDefined ) {
          if( stolen.get.isDefined )
            TUI.outln( "You stole 1 " + stolen.get.get.s + "." )
          else
            TUI.outln( "Your stole nothing." )
          TUI.awaitKey()
        }
        Option.empty
      case Failure( e ) => Some( e )
    }
  }
}
