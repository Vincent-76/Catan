package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.aview.gui.commands.StartTurnCommand
import de.htwg.se.settlers.aview.gui.{ GUICommand, GUIState }

/**
 * @author Vincent76;
 */
case class NextPlayerGUIState( controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( StartTurnCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
