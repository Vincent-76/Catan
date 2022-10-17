package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import com.aimit.htwg.catan.model.impl.placement.RoadPlacement
import com.aimit.htwg.catan.model.state.DevRoadBuildingState
import com.aimit.htwg.catan.model.{ Command, _ }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object DevBuildRoadCommand extends CommandImpl( "DevBuildRoadCommand" ) {
  override def fromXML( node:Node ):DevBuildRoadCommand = DevBuildRoadCommand(
    eID = ( node \ "@eID" ).content.toInt,
    state = DevRoadBuildingState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):DevBuildRoadCommand = DevBuildRoadCommand(
    eID = ( json \ "eID" ).as[Int],
    state = DevRoadBuildingState.fromJson( ( json \ "state" ).get )
  )
}

case class DevBuildRoadCommand( eID:Int, state:DevRoadBuildingState ) extends Command {

  def toXML:Node = <DevBuildRoadCommand eID={ eID.toString }>
    <state>{ state.toXML }</state>
  </DevBuildRoadCommand>.copy( label = DevBuildRoadCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DevBuildRoadCommand.name ),
    "eID" -> Json.toJson( eID ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    RoadPlacement.build( game, game.onTurn, eID ) match {
      case Failure( t ) => Failure( t )
      case Success( newGame ) =>
        val (nextState, info) = if ( !newGame.player.hasStructure( RoadPlacement ) )
          (state.nextState, InsufficientStructuresInfo( newGame.onTurn, RoadPlacement ))
        else if ( RoadPlacement.getBuildablePoints( newGame, newGame.onTurn ).isEmpty )
          (state.nextState, NoPlacementPointsInfo( newGame.onTurn, RoadPlacement ))
        else if ( state.roads == 0 )
          (DevRoadBuildingState( state.nextState, state.roads + 1 ), BuiltInfo( RoadPlacement, eID ))
        else
          (state.nextState, BuiltInfo( RoadPlacement, eID ))
        success( newGame.setState( nextState ), info = Some( info ) )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )
    .setGameField( game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( None ) ) )
    .updatePlayer( game.player.addStructure( RoadPlacement ) )

  //override def toString:String = getClass.getSimpleName + ": eID[" + eID + "], " + state
}
