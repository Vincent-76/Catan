package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object PlayerTradeCommand extends TradeCommand( "Player Trade" ) {

  override def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit =
    gui.controller.game.state.setPlayerTradeState( give, get )
}