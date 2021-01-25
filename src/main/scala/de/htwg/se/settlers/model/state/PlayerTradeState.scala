package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.PlayerTradeDecisionCommand

/**
 * @author Vincent76;
 */
case class PlayerTradeState( controller:Controller,
                             pID:PlayerID,
                             give:ResourceCards,
                             get:ResourceCards,
                             decisions:Map[PlayerID, Boolean] ) extends State( controller ) {

  override def playerTradeDecision( decision:Boolean ):Unit = controller.action(
    PlayerTradeDecisionCommand( decision, this )
  )
}
