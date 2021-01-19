package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.DiceState
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.collection.immutable.SortedMap
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class GatherCommand( state:DiceState, playerResources:Map[PlayerID, ResourceCards] ) extends Command {

  private var availablePlayerResources:Map[PlayerID, ResourceCards] = Map.empty // ???

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    val (newPlayers, newStack) = playerResources.red( (game.players, game.resourceStack),
      ( data:(SortedMap[PlayerID, Player], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
        val (nCards, nStack) = game.getAvailableResourceCards( cards, data._2 )
        if ( nCards.amount != cards.amount )
          return Failure( InconsistentData )
        (data._1.updated( pID, game.players( pID ).addResourceCards( cards ) ), nStack)
      } )
    Success( (game.copy(
      state = controller.ui.getActionState,
      players = newPlayers,
      resourceStack = newStack,
    ), Some( GatherInfo( state.dices, playerResources ) )) )
  }

  override def undoStep( game:Game ):Game = {
    val (newPlayers, newStack) = playerResources.red( (game.players, game.resourceStack),
      ( data:(SortedMap[PlayerID, Player], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
        (data._1.updated( pID, data._1( pID ).removeResourceCards( cards ).get ), data._2.add( cards ))
      } )
    game.copy(
      state = state,
      players = newPlayers,
      resourceStack = newStack
    )
  }
}
