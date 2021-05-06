package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.GameField.{Hex, Row, Vertex}
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state.{ActionState, DropHandCardsState, RobberPlaceState}
import de.htwg.se.settlers.util._

import scala.collection.immutable.SortedMap
import scala.util.{Failure, Success, Try}

/**
 * @author Vincent76;
 */
case class RollDicesCommand( state:State ) extends Command {

  private var availablePlayerResources:Option[Map[PlayerID, ResourceCards]] = Option.empty

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    val dices = game.rollDices()
    println( "Dices[" + dices._1 + " + " + dices._2 + " = " + ( dices._1 + dices._2 ) + "]" )
    Numbers.of( dices._1 + dices._2 ) match {
      //case None => Failure( Fail )
      case Some( Seven ) => game.checkHandCardsInOrder() match {
        case Some( p ) => Success( game.setState( DropHandCardsState( p.id ) ), Some( DiceInfo( dices ) ) )
        case None => Success( game.setState( RobberPlaceState( ActionState() ) ), Some( DiceInfo( dices ) ) )
      }
      case Some( n:Number ) =>
        val playerResources = game.gameField.hexagons.red( Map.empty[PlayerID, ResourceCards], ( resources:Map[PlayerID, ResourceCards], row:Row[Hex] ) => {
          row.red( resources, ( resources:Map[PlayerID, ResourceCards], hex:Option[Hex] ) => {
            if ( hex.isDefined && hex.get != game.gameField.robber ) hex.get.area match {
              case r:ResourceArea if r.number == n =>
                game.gameField.adjacentVertices( hex.get ).red( resources, ( resources:Map[PlayerID, ResourceCards], v:Vertex ) =>
                  v.building match {
                    case Some( v:Settlement ) =>
                      resources.updated( v.owner, resources.getOrElse( v.owner, Cards.getResourceCards( 0 ) ).add( r.resource ) )
                    case Some( c:City ) =>
                      resources.updated( c.owner, resources.getOrElse( c.owner, Cards.getResourceCards( 0 ) ).add( r.resource, 2 ) )
                    case _ => resources
                  } )
              case _ => resources
            }
            else resources
          } )
        } )
        val (available, newStack) = playerResources.red( (playerResources, game.resourceStack),
          ( data:(Map[PlayerID, ResourceCards], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
            val (available, newStack) = game.getAvailableResourceCards( cards, data._2 )
            if ( available.amount > 0 )
              (data._1.updated( pID, available ), newStack)
            else
              (data._1 - pID, data._2)
          }
        )
        availablePlayerResources = if( available.isEmpty ) None else Some( available )
        Success( (game.copy(
          state = ActionState(),
          players = game.updatePlayers( available.map( d => game.player( d._1 ).addResourceCards( d._2 ) ).toList:_* ),
          resourceStack = newStack,
        ), Some( GatherInfo( dices, playerResources ) )) )
    }
  }

  override def undoStep( game:Game ):Game = availablePlayerResources match {
    case Some( available ) =>
      val (newPlayers, newStack) = available.red( (game.players, game.resourceStack),
        ( data:(SortedMap[PlayerID, Player], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
          (data._1.updated( pID, data._1( pID ).removeResourceCards( cards ).get ), data._2.add( cards ))
        } )
      game.copy(
        state = state,
        players = newPlayers,
        resourceStack = newStack
      )
    case None => game.setState( state )
  }

  /*override def toString:String = getClass.getSimpleName + ": availablePlayerResources[" +
    availablePlayerResources.useOrElse( d => d.map( d => d._1 + "[" + d._2 + "]" ).mkString( ", " ), "-" ) +
    "], " + state*/
}
