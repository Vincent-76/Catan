package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, TUI, TUIState }
import de.htwg.se.settlers.controller.Controller

/**
 * @author Vincent76;
 */
case class BuildInitRoadTUIState( vID:Int, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = {
    Some( getGameDisplay( controller, controller.game.getBuildableRoadSpotsForSettlement( vID ) ).buildGameField )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + " place your road." )
    "Select position [<id>] for your road"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.buildInitRoad( commandInput.command.get.toInt )
}
