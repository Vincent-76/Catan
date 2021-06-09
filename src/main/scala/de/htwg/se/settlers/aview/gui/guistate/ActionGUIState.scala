package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.gui.commands.{BankTradeCommand, BuildCommand, BuyDevCardCommand, EndTurnCommand, PlayerTradeCommand, UseDevCardCommand}
import de.htwg.se.settlers.aview.gui.{GUICommand, GUIState}
import de.htwg.se.settlers.model.player.Player

/**
 * @author Vincent76;
 */
case class ActionGUIState( controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List(
    BuildCommand,
    BankTradeCommand,
    PlayerTradeCommand,
    BuyDevCardCommand,
    UseDevCardCommand,
    EndTurnCommand
  )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
