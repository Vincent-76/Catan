package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.{ AbortPlayerTradeCommand, PlayerTradeCommand }

/**
 * @author Vincent76;
 */
case class PlayerTradeEndState( controller:Controller,
                                give:ResourceCards,
                                get:ResourceCards,
                                decisions:Map[PlayerID, Boolean] ) extends State( controller ) {

  override def playerTrade( tradePlayerID:PlayerID ):Unit = controller.action(
    PlayerTradeCommand( tradePlayerID, this )
  )

  override def abortPlayerTrade( ):Unit = controller.action(
    AbortPlayerTradeCommand( this )
  )

}
