package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object BankTradeCommand extends TradeCommand( "Bank Trade" ) {

  override def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit =
    gui.controller.game.state.bankTrade( give, get )
}
