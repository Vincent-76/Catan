package de.htwg.se.settlers.ui.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.ui.gui.commands.{ RollDicesCommand, UseDevCardCommand }
import de.htwg.se.settlers.ui.gui.{ DisplayState, FieldDisplayState, GUICommand, GUIState }

/**
 * @author Vincent76;
 */
case class DiceGUIState( controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( RollDicesCommand, UseDevCardCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
