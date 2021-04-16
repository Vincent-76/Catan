package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.PlayerTradeDecisionCommand
import de.htwg.se.settlers.util._

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

  override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], give[" + give + "], get[" + get +
    "], Decisions[" + decisions.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "]"
}
