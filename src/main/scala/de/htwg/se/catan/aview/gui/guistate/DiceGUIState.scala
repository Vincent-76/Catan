package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.commands.{ RollDicesCommand, UseDevCardCommand }
import de.htwg.se.catan.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */
case class DiceGUIState( gui:GUI ) extends GUIState:
  override def getActions:List[GUICommand] = List( RollDicesCommand, UseDevCardCommand )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )
