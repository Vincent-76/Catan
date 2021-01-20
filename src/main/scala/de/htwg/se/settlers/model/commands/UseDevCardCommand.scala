package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class UseDevCardCommand( devCard:DevelopmentCard, state:State ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( game.turn.usedDevCard )
      return Failure( AlreadyUsedDevCardInTurn )
    else if( !game.player.devCards.contains( devCard ) )
      return Failure( InsufficientDevCards( devCard ) )
    else if ( game.player.devCards.count( _ == devCard ) <= game.turn.drawnDevCards.count( _ == devCard ) )
      return Failure( DevCardDrawnInTurn( devCard ) )
    val newPlayer = game.player.useDevCard( devCard )
    if ( newPlayer.isFailure )
      return newPlayer.rethrow
    val nextState = devCard match {
      case KnightCard => controller.ui.getRobberPlaceState( state )
      case YearOfPlentyCard => controller.ui.getYearOfPlentyState( state )
      case RoadBuildingCard =>
        if ( !game.player.hasStructure( Road ) )
          return Failure( InsufficientStructures( Road ) )
        if ( game.getBuildableIDsForPlayer( game.onTurn, Road ).isEmpty )
          return Failure( NoPlacementPoints( Road ) )
        controller.ui.getDevRoadBuildingState( state )
      case MonopolyCard => controller.ui.getMonopolyState( state )
      case _ => state
    }
    val newBonusCards = if ( devCard == KnightCard ) {
      val amount = newPlayer.get.usedDevCards.count( _ == KnightCard )
      val largestArmy = game.bonusCards( LargestArmyCard )
      if ( amount >= LargestArmyCard.required && ( largestArmy.isEmpty || amount > largestArmy.get._2 ) )
        game.bonusCards.updated( LargestArmyCard, Some( newPlayer.get.id, amount ) )
      else game.bonusCards
    } else game.bonusCards
    Success( game.copy(
      state = nextState,
      turn = game.turn.copy( usedDevCard = true ),
      players = game.players.updated( newPlayer.get.id, newPlayer.get ),
      bonusCards = newBonusCards,
    ), Option.empty )
  }

  override def undoStep( game:Game ):Game = {
    val newGame = game.copy(
      state = state,
      players = game.players.updated( game.onTurn, game.player.copy(
        devCards = game.player.devCards :+ devCard,
        usedDevCards = game.player.usedDevCards.removed( devCard ).toVector
      ) )
    )
    if ( devCard == KnightCard ) {
      val largestArmy = game.bonusCards( LargestArmyCard )
      if ( largestArmy.isDefined && largestArmy.get._1 == game.onTurn ) {
        val maxArmy = newGame.players.map( _._2.usedDevCards.count( _ == KnightCard ) ).zipWithIndex.maxBy( _._1 )
        val newLargestArmy = if ( maxArmy._2 >= LargestArmyCard.required )
          Some( game.onTurn, maxArmy._1 )
        else
          Option.empty
        newGame.copy( bonusCards = game.bonusCards.updated( LargestArmyCard, newLargestArmy ) )
      }
    }
    newGame
  }
}
