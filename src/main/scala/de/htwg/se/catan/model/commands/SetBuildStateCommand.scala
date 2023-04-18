package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.state.BuildState
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.error.{ InsufficientStructures, NoPlacementPoints, UnavailableStructure }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.info.LostResourcesInfo
import de.htwg.se.catan.util.*
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetBuildStateCommand extends CommandImpl( "SetBuildStateCommand" ):
  override def fromXML( node:Node ):SetBuildStateCommand = SetBuildStateCommand(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get,
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):SetBuildStateCommand = SetBuildStateCommand(
    structure = ( json \ "structure" ).as[StructurePlacement],
    state = ( json \ "state" ).as[State]
  )


case class SetBuildStateCommand( structure:StructurePlacement, state:State ) extends Command:

  def toXML:Node = <SetBuildStateCommand structure={ structure.title }>
    <state>{ state.toXML }</state>
  </SetBuildStateCommand>.copy( label = SetBuildStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetBuildStateCommand.name ),
    "structure" -> Json.toJson( structure ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] =
    if !game.availablePlacements.contains( structure ) then
      Failure( UnavailableStructure( structure ) )
    else if !game.player.hasStructure( structure ) then
      Failure( InsufficientStructures( structure ) )
    else if structure.getBuildablePoints( game, game.onTurn ).isEmpty then
      Failure( NoPlacementPoints( structure ) )
    else game.dropResourceCards( game.onTurn, structure.resources ) match
      case Success( newGame ) => success(
        newGame.setState( BuildState( structure ) ),
        info = Some( LostResourcesInfo( game.onTurn, structure.resources ) )
      )
      case f => f.rethrow

  override def undoStep( game:Game ):Game =
    game.setState( state ).drawResourceCards( game.onTurn, structure.resources )._1

  //override def toString:String = getClass.getSimpleName + ": structure[" + structure.title + "], " + state
