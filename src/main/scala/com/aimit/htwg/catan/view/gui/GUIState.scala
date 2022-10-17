package com.aimit.htwg.catan.view.gui

import com.aimit.htwg.catan.model.Player

/**
 * @author Vincent76;
 */

trait GUIState {
  def getDisplayState:DisplayState = FieldDisplayState

  def getActions:List[GUICommand] = Nil

  def playerDisplayed:Option[(Player, Boolean)] = None
}



