package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.catan.controller.Controller

/**
 * @author Vincent76;
 */
case class BuildInitRoadTUIState( vID:Int, controller:Controller ) extends TUIState:

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( controller.game.getBuildableRoadSpotsForSettlement( vID ) ) ).buildGameField
  )

  override def getActionInfo:String =
    TUI.outln( TUI.displayName( controller.game.player ) + " place your road." )
    "Select position [<id>] for your road"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.buildInitRoad( commandInput.command.get.toInt )
