package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.GameField.{ Hex, Vertex }
import de.htwg.se.settlers.model.state.{ BuildInitRoadState, BuildInitSettlementState }
import de.htwg.se.settlers.model.{ Command, Game, GotResourcesInfo, Info, Resource, Settlement }
import de.htwg.se.settlers.util._

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class BuildInitSettlementCommand( vID:Int, state:BuildInitSettlementState ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    Settlement.build( game, game.onTurn, vID, anywhere = true ) match {
      case Success( game ) =>
        if ( game.settlementAmount( game.onTurn ) == 2 ) {
          val resources = adjacentResources( game.gameField.findVertex( vID ).get )
          Success(
            game.drawResourceCards( game.onTurn, resources )
              .setState( BuildInitRoadState( vID ) ),
            Some( GotResourcesInfo( game.onTurn, resources ) )
          )
        }
        else Success( game.setState( BuildInitRoadState( vID ) ), None )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    val v = game.gameField.findVertex( vID ).get
    val newGame = if( game.settlementAmount( game.onTurn ) == 2 )
      game.dropResourceCards( game.onTurn, adjacentResources( v ) ).get
    else game
    newGame.copy(
      state = state,
      gameField = newGame.gameField.update( v.setBuilding( None ) ),
      players = newGame.updatePlayers( newGame.player.addStructure( Settlement ) )
    )
  }

  private def adjacentResources( v:Vertex ):ResourceCards = {
    List( v.h1, v.h2, v.h3 ).red( Map.empty:ResourceCards, ( c:ResourceCards, h:Hex ) => {
      h.area.f match {
        case r:Resource => c.add( r )
        case _ => c
      }
    } )
  }

  //override def toString:String = getClass.getSimpleName + ": vID[" + vID + "], " + state
}
