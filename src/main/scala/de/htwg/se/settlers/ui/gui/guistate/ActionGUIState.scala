package de.htwg.se.settlers.ui.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.ui.gui.commands.{ BankTradeCommand, BuildCommand, BuyDevCardCommand, EndTurnCommand, PlayerTradeCommand, UseDevCardCommand }
import de.htwg.se.settlers.ui.gui.{ GUICommand, GUIState }

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
