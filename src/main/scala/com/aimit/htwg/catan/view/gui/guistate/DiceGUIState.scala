package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.commands.{ RollDicesCommand, UseDevCardCommand }
import com.aimit.htwg.catan.view.gui.{ GUICommand, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player

/**
 * @author Vincent76;
 */
case class DiceGUIState( controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( RollDicesCommand, UseDevCardCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
