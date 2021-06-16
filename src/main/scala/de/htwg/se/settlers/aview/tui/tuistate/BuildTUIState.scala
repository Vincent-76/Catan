package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, GameFieldDisplay, TUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.StructurePlacement

/**
 * @author Vincent76;
 */
case class BuildTUIState( structure:StructurePlacement, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( structure.getBuildablePoints( controller.game, controller.onTurn ) ) ).buildGameField
  )

  override def getActionInfo:String = "Select position [<id>] for your " + structure.title

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.build( commandInput.input.toInt )
}
