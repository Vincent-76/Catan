package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.{ AbortPlayerTradeCommand, PlayerTradeCommand }

/**
 * @author Vincent76;
 */
abstract class PlayerTradeEndState( val give:ResourceCards,
                                    val get:ResourceCards,
                                    val decisions:Map[PlayerID, Boolean],
                                    controller:Controller ) extends State( controller ) {

  override def playerTrade( tradePlayerID:PlayerID ):Unit = controller.action(
    PlayerTradeCommand( tradePlayerID, this )
  )

  override def abortPlayerTrade( ):Unit = controller.action(
    AbortPlayerTradeCommand( this )
  )

}
