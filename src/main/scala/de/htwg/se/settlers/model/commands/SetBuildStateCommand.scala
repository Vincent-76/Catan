package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.BuildState
import de.htwg.se.settlers.util._
import de.htwg.se.settlers.model.{ Command, Game, Info, InsufficientStructures, LostResourcesInfo, NoPlacementPoints, State, StructurePlacement }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class SetBuildStateCommand( structure:StructurePlacement, state:State ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( !game.player.hasStructure( structure ) )
      Failure( InsufficientStructures( structure ) )
    else if ( structure.getBuildablePoints( game, game.onTurn ).isEmpty )
      Failure( NoPlacementPoints( structure ) )
    else game.dropResourceCards( game.onTurn, structure.resources ) match {
      case Success( newGame ) => Success(
        newGame.setState( BuildState( controller, structure ) ),
        Some( LostResourcesInfo( game.onTurn, structure.resources ) ) )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    game.drawResourceCards( game.onTurn, structure.resources ).setState( state )
  }

  override def toString:String = getClass.getSimpleName + ": structure[" + structure.title + "], " + state
}
