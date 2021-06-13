package de.htwg.se.settlers.aview.tui

import de.htwg.se.settlers.aview.tui.impl.gamefield.ClassicGameDisplayImpl
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.PlacementPoint
import de.htwg.se.settlers.model.impl.game.ClassicGameImpl

/**
 * @author Vincent76;
 */
trait TUIState {
  def getGameDisplay( controller:Controller ):GameDisplay[_] = controller.game match {
    case _:ClassicGameImpl => ClassicGameDisplayImpl( controller )
    case c => throw new NotImplementedError( "GameDisplay[" + c.getClass.getName + "]" )
  }

  def getGameDisplay( controller:Controller, buildableIDs:List[PlacementPoint] ):GameDisplay[_] = controller.game match {
    case _:ClassicGameImpl => ClassicGameDisplayImpl( controller, buildableIDs )
    case c => throw new NotImplementedError( "GameDisplay[" + c.getClass.getName + "]" )
  }

  def createGameDisplay:Option[String] = None

  def getActionInfo:String

  def inputPattern:Option[String] = None

  def action( commandInput: CommandInput ):Unit
}
