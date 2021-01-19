package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.GameField.{ Hex, Vertex }
import de.htwg.se.settlers.model.state.BuildInitSettlementState
import de.htwg.se.settlers.model.{ Command, Game, GotResourcesInfo, Info, Resource, Settlement }
import de.htwg.se.settlers.util._

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class BuildInitSettlementCommand( vID:Int, state:BuildInitSettlementState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {

    Settlement.build( game, game.onTurn, vID, anywhere = true ) match {
      case Success( game ) =>
        if ( game.settlementAmount( game.onTurn ) == 2 ) {
          val resources = adjacentResources( game.gameField.findVertex( vID ).get )
          Success(
            game.drawResourceCards( game.onTurn, resources )
              .setState( controller.ui.getBuildInitRoadState( vID ) ),
            Some( GotResourcesInfo( game.onTurn, resources ) )
          )
        }
        else Success( game.setState( controller.ui.getBuildInitRoadState( vID ) ), Option.empty )
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
      gameField = newGame.gameField.update( v.setBuilding( Option.empty ) ),
      players = newGame.updatePlayers( newGame.player.addStructure( Settlement ) )
    )
  }

  private def adjacentResources( v:Vertex ):ResourceCards = {
    v.hexes.red( Map.empty:ResourceCards, ( c:ResourceCards, h:Hex ) => {
      h.area.f match {
        case r:Resource => c.add( r )
        case _ => c
      }
    } )
  }
}
