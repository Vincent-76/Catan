package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.{ AbortPlayerTradeCommand, PlayerTradeCommand }

/**
 * @author Vincent76;
 */
case class PlayerTradeEndState( give:ResourceCards,
                                get:ResourceCards,
                                decisions:Map[PlayerID, Boolean] ) extends State {

  override def playerTrade( tradePlayerID:PlayerID ):Option[Command] = Some(
    PlayerTradeCommand( tradePlayerID, this )
  )

  override def abortPlayerTrade( ):Option[Command] = Some(
    AbortPlayerTradeCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": Give[" + give + "], Get[" + get +
    "], Decisions[" + decisions.map( d => d._1.id + ": " + d._2 ).mkString( ", " ) + "]"*/
}
