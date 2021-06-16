package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object PlayerTradeCommand extends TradeCommand( "Player Trade" ) {

  override def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit =
    gui.controller.setPlayerTradeState( give, get )
}
