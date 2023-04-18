package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.gui.commands.{ BankTradeCommand, BuildCommand, BuyDevCardCommand, EndTurnCommand, PlayerTradeCommand, UseDevCardCommand }
import de.htwg.se.catan.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */
case class ActionGUIState( gui:GUI ) extends GUIState:

  override def getActions:List[GUICommand] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCardCommand,
    UseDevCardCommand,
    EndTurnCommand
  )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )