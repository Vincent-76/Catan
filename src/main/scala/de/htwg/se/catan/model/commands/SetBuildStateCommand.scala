package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.state.BuildState
import de.htwg.se.catan.model._
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetBuildStateCommand extends CommandImpl( "SetBuildStateCommand" ) {
  override def fromXML( node:Node ):SetBuildStateCommand = ???

  override def fromJson( json:JsValue ):SetBuildStateCommand = ???
}

case class SetBuildStateCommand( structure:StructurePlacement, state:State ) extends Command {

  def toXML:Node = <SetBuildStateCommand>
    <state>{ state.toXML }</state>
  </SetBuildStateCommand>.copy( label = SetBuildStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetBuildStateCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.availablePlacements.contains( structure ) )
      Failure( UnavailableStructure( structure ) )
    else if( !game.player.hasStructure( structure ) )
      Failure( InsufficientStructures( structure ) )
    else if( structure.getBuildablePoints( game, game.onTurn ).isEmpty )
      Failure( NoPlacementPoints( structure ) )
    else game.dropResourceCards( game.onTurn, structure.resources ) match {
      case Success( newGame ) => success(
        newGame.setState( BuildState( structure ) ),
        info = Some( LostResourcesInfo( game.onTurn, structure.resources ) )
      )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    game.setState( state ).drawResourceCards( game.onTurn, structure.resources )._1
  }

  //override def toString:String = getClass.getSimpleName + ": structure[" + structure.title + "], " + state
}
