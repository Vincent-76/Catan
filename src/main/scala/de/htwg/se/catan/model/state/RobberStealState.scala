package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.RobberStealCommand

/**
 * @author Vincent76;
 */
case class RobberStealState( adjacentPlayers:List[PlayerID],
                             nextState:State ) extends State {

  override def robberStealFromPlayer( stealPlayerID:PlayerID ):Option[Command] = Some(
    RobberStealCommand( stealPlayerID, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": adjacentPlayers[" + adjacentPlayers.mkString( ", " ) +
    "], NextState[" + nextState + "]"*/
}
