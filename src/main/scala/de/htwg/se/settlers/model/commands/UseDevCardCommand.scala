package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state.{DevRoadBuildingState, MonopolyState, RobberPlaceState, YearOfPlentyState}
import de.htwg.se.settlers.util._

import scala.util.{Failure, Success, Try}

/**
 * @author Vincent76;
 */
case class UseDevCardCommand( devCard:DevelopmentCard, state:State ) extends Command {

  var actualBonusCards:Option[Map[BonusCard, Option[(PlayerID, Int)]]] = None

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( game.turn.usedDevCard )
      return Failure( AlreadyUsedDevCardInTurn )
    val newPlayer = game.player.useDevCard( devCard )
    if ( newPlayer.isFailure )
      return newPlayer.rethrow
    if ( game.player.devCards.count( _ == devCard ) <= game.turn.drawnDevCards.count( _ == devCard ) )
      return Failure( DevCardDrawnInTurn( devCard ) )
    val nextState = devCard match {
      case KnightCard => RobberPlaceState( state )
      case YearOfPlentyCard => YearOfPlentyState( state )
      case RoadBuildingCard =>
        if ( !game.player.hasStructure( Road ) )
          return Failure( InsufficientStructures( Road ) )
        if ( Road.getBuildablePoints( game, game.onTurn ).isEmpty )
          return Failure( NoPlacementPoints( Road ) )
        DevRoadBuildingState( state )
      case MonopolyCard => MonopolyState( state )
      //case _ => state
    }
    val newBonusCards = if ( devCard == KnightCard ) {
      val amount = newPlayer.get.usedDevCards.count( _ == KnightCard )
      val largestArmy = game.bonusCards( LargestArmyCard )
      if ( amount >= LargestArmyCard.required && ( largestArmy.isEmpty || amount > largestArmy.get._2 ) )
        game.bonusCards.updated( LargestArmyCard, Some( newPlayer.get.id, amount ) )
      else game.bonusCards
    } else game.bonusCards
    actualBonusCards = Some( game.bonusCards )
    Success( game.copy(
      state = nextState,
      turn = game.turn.copy( usedDevCard = true ),
      players = game.players.updated( newPlayer.get.id, newPlayer.get ),
      bonusCards = newBonusCards,
    ), None )
  }

  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    players = game.players.updated( game.onTurn, game.player.copy(
      devCards = game.player.devCards :+ devCard,
      usedDevCards = game.player.usedDevCards.removed( devCard ).toVector
    ) ),
    turn = game.turn.copy( usedDevCard = false ),
    bonusCards = actualBonusCards.getOrElse( game.bonusCards )
  )

  //override def toString:String = getClass.getSimpleName + ": devCard[" + devCard + "], " + state
}
