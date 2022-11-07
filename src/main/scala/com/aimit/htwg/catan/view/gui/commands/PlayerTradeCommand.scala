package com.aimit.htwg.catan.view.gui.commands

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.view.gui.GUI

/**
 * @author Vincent76;
 */
case object PlayerTradeCommand extends TradeCommand( "Player Trade" ) {

  override def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit =
    gui.controller.action( _.setPlayerTradeState( give, get ) )
}
