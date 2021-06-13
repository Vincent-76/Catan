package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ Command, PlayerID, State }
import de.htwg.se.settlers.model.commands.PlayerTradeDecisionCommand

/**
 * @author Vincent76;
 */
case class PlayerTradeState( pID:PlayerID,
                             give:ResourceCards,
                             get:ResourceCards,
                             decisions:Map[PlayerID, Boolean] ) extends State {

  override def playerTradeDecision( decision:Boolean ):Option[Command] = Some(
    PlayerTradeDecisionCommand( decision, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], give[" + give + "], get[" + get +
    "], Decisions[" + decisions.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "]"*/
}
