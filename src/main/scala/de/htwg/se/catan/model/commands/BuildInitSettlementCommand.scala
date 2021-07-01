package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.impl.placement.SettlementPlacement
import de.htwg.se.catan.model.state.{ BuildInitRoadState, BuildInitSettlementState }
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildInitSettlementCommand extends CommandImpl( "BuildInitSettlementCommand" ) {
  override def fromXML( node:Node ):BuildInitSettlementCommand = BuildInitSettlementCommand(
    vID = ( node \ "@vID" ).content.toInt,
    state = BuildInitSettlementState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):BuildInitSettlementCommand = BuildInitSettlementCommand(
    vID = ( json \ "vID" ).as[Int],
    state = BuildInitSettlementState.fromJson( ( json \ "state" ).get )
  )
}

case class BuildInitSettlementCommand( vID:Int, state:BuildInitSettlementState ) extends Command {

  def toXML:Node = <BuildInitSettlementCommand vID={ vID.toString }>
    <state>{ state.toXML }</state>
  </BuildInitSettlementCommand>.copy( label = BuildInitSettlementCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuildInitSettlementCommand.name ),
    "vID" -> Json.toJson( vID ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    SettlementPlacement.build( game, game.onTurn, vID, anywhere = true ) match {
      case Success( game ) =>
        if ( game.settlementAmount( game.onTurn ) == 2 ) {
          val resources = adjacentResources( game.gameField.findVertex( vID ).get )
          success(
            game.setState( BuildInitRoadState( vID ) )
              .drawResourceCards( game.onTurn, resources )._1,
            Some( GotResourcesInfo( game.onTurn, resources ) )
          )
        }
        else success( game.setState( BuildInitRoadState( vID ) ), None )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    val v = game.gameField.findVertex( vID ).get
    val newGame = if( game.settlementAmount( game.onTurn ) == 2 )
      game.dropResourceCards( game.onTurn, adjacentResources( v ) ).get
    else game
    newGame.setState( state )
      .setGameField( newGame.gameField.update( v.setBuilding( None ) ) )
      .updatePlayer( newGame.player.addStructure( SettlementPlacement ) )
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
