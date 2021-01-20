package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.MonopolyState
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.collection.immutable.SortedMap
import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class MonopolyCommand( r:Resource, state:MonopolyState ) extends Command {

  var robbedResources:Option[Map[PlayerID, Int]] = Option.empty

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    val newData = game.players.red( (game.players, Map.empty[PlayerID, Int]),
      ( data:(SortedMap[PlayerID, Player], Map[PlayerID, Int]), pID:PlayerID, p:Player ) => {
        if ( pID != game.onTurn ) {
          val amount = p.resources.getOrElse( r, 0 )
          (data._1.updated( pID, p.removeResourceCard( r, amount ).get ), data._2.updated( pID, amount ))
        } else data
      } )
    robbedResources = Some( newData._2 )
    val amount = newData._2.red( 0, ( i:Int, _:PlayerID, a:Int ) => i + a )
    Success( game.copy(
      state = state.nextState,
      players = newData._1.updated( game.onTurn, game.player.addResourceCard( r, amount ) )
    ), Some( ResourceChangeInfo(
      playerAdd = Map( game.onTurn -> ResourceCards.ofResource( r, amount ) ),
      playerSub = newData._2.map( d => (d._1, ResourceCards.ofResource( r, d._2 )) )
    ) ) )
  }

  override def undoStep( game:Game ):Game = robbedResources match {
    case None => game.setState( state )
    case Some( playerResources ) =>
      val amount = playerResources.red( 0, ( i:Int, _:PlayerID, a:Int ) => i + a )
      val newPlayers = game.updatePlayers( playerResources.map( d => game.players( d._1 ).addResourceCard( r, d._2 ) ).toSeq:_* )
      game.copy(
        state = state,
        players = newPlayers.updated( game.onTurn, game.player.removeResourceCard( r, amount ).get )
      )
  }
}
