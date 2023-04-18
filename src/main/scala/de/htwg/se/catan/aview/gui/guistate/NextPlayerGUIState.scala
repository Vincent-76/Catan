package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.commands.StartTurnCommand
import de.htwg.se.catan.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */
case class NextPlayerGUIState( gui:GUI ) extends GUIState:
  override def getActions:List[GUICommand] = List( StartTurnCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, false )