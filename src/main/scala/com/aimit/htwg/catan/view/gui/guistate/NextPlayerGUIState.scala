package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.gui.commands.StartTurnCommand
import com.aimit.htwg.catan.view.gui.{ GUICommand, GUIState }
import com.aimit.htwg.catan.model.Player

/**
 * @author Vincent76;
 */
case class NextPlayerGUIState( controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( StartTurnCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
