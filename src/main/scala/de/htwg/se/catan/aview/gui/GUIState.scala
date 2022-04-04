package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */

trait GUIState:
  def getDisplayState:DisplayState = FieldDisplayState

  def getActions:List[GUICommand] = Nil

  def playerDisplayed:Option[(Player, Boolean)] = None