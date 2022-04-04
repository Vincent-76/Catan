package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.gui.commands.{ BankTradeCommand, BuildCommand, BuyDevCardCommand, EndTurnCommand, PlayerTradeCommand, UseDevCardCommand }
import de.htwg.se.catan.aview.gui.{ GUICommand, GUIState }
import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */
case class ActionGUIState( controller:Controller ) extends GUIState:

  override def getActions:List[GUICommand] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCardCommand,
    UseDevCardCommand,
    EndTurnCommand
  )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )