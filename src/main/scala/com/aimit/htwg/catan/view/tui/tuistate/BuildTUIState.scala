package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Info, StructurePlacement }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class BuildTUIState( structure:StructurePlacement, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( structure.getBuildablePoints( controller.game, controller.onTurn ) ) ).buildGameField
  )

  override def getActionInfo:String = "Select position [<id>] for your " + structure.name

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.action( _.build( commandInput.input.toInt ) ), Nil)
}
