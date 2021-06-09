package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.Game
import de.htwg.se.settlers.model.player.Player

/**
 * @author Vincent76;
 */

trait GUIState {
  def getDisplayState:DisplayState = FieldDisplayState

  def getActions:List[GUICommand] = Nil

  def playerDisplayed:Option[(Player, Boolean)] = Option.empty
}



