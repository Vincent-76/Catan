package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import de.htwg.se.catan.model.state.RobberStealState
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberStealCommand extends CommandImpl( "RobberStealCommand" ):
  override def fromXML( node:Node ):RobberStealCommand =
    val cmd = RobberStealCommand(
      stealPlayerID = PlayerID.fromXML( node.childOf( "stealPlayerID" ) ),
      state = RobberStealState.fromXML( node.childOf( "state" ) )
    )
    cmd.robbedResource = node.childOf( "robbedResource" ).asOption( n => Resource.of( n.content ).get )
    cmd

  override def fromJson( json:JsValue ):RobberStealCommand =
    val cmd = RobberStealCommand(
      stealPlayerID = ( json \ "stealPlayerID" ).as[PlayerID],
      state = RobberStealState.fromJson( ( json \ "state" ).get )
    )
    cmd.robbedResource = ( json \ "robbedResource" ).asOption[Resource]
    cmd
  

case class RobberStealCommand( stealPlayerID:PlayerID, state:RobberStealState ) extends RobberCommand:

  def toXML:Node = <RobberStealCommand>
    <stealPlayerID>{ stealPlayerID.toXML }</stealPlayerID>
    <state>{ state.toXML }</state>
    <robbedResource>{ robbedResource.toXML( _.title ) }</robbedResource>
  </RobberStealCommand>.copy( label = RobberStealCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RobberStealCommand.name ),
    "stealPlayerID" -> Json.toJson( stealPlayerID ),
    "state" -> state.toJson,
    "robbedResources" -> Json.toJson( robbedResource )
  )

  override def doStep( game:Game ):Try[CommandSuccess] =
    if !game.playerHasAdjacentVertex( stealPlayerID, game.gameField.adjacentVertices( game.gameField.robberHex ) ) then
      Failure( NoAdjacentStructure )
    else steal( game, stealPlayerID, state.nextState )

  override def undoStep( game:Game ):Game = robbedResource match
    case Some( r ) => game.setState( state )
      .updatePlayers(
        game.player.removeResourceCard( r ).get,
        game.players( stealPlayerID ).addResourceCard( r )
      )
    case None => game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) +  "], stealPlayerID[" + stealPlayerID + "], " + state
