package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object BankTradeCommand extends TradeCommand( "Bank Trade" ) {

  override def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit =
    gui.controller.bankTrade( give, get )
}
