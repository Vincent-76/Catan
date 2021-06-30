package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import de.htwg.se.catan.model.state.{ RobberPlaceState, RobberStealState }
import de.htwg.se.catan.model.{ Hex, _ }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlaceRobberCommand extends CommandImpl( "PlaceRobberCommand" ) {
  override def fromXML( node:Node ):PlaceRobberCommand = {
    val cmd = PlaceRobberCommand(
      hID = ( node \ "@hID" ).content.toInt,
      state = RobberPlaceState.fromXML( node.childOf( "state" ) )
    )
    cmd.robbedResource = node.childOf( "robbedResource" ).asOption( n => Resource.of( n.content ).get )
    cmd
  }

  override def fromJson( json:JsValue ):PlaceRobberCommand = {
    val cmd = PlaceRobberCommand(
      hID = ( json \ "hID" ).as[Int],
      state = RobberPlaceState.fromJson( ( json \ "state" ).get )
    )
    cmd.robbedResource = ( json \ "robbedResource" ).asOption[Resource]
    cmd
  }
}

case class PlaceRobberCommand( hID:Int, state:RobberPlaceState ) extends RobberCommand {

  def toXML:Node = <PlaceRobberCommand hID={ hID.toString }>
    <state>{ state.toXML }</state>
    <robbedResource>{ robbedResource.toXML( _.title ) }</robbedResource>
  </PlaceRobberCommand>.copy( label = PlaceRobberCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlaceRobberCommand.name ),
    "hID" -> Json.toJson( hID ),
    "state" -> state.toJson,
    "robbedResources" -> Json.toJson( robbedResource )
  )

  private var actualRobber:Option[Hex] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val hex = game.gameField.findHex( hID )
    if( hex.isEmpty )
      Failure( NonExistentPlacementPoint( hID ) )
    else if( hex.get == game.gameField.robberHex )
      Failure( PlacementPointNotEmpty( hID ) )
    else if( !hex.get.isLand )
      Failure( RobberOnlyOnLand )
    else {
      actualRobber = Some( game.gameField.robberHex )
      val newGameField = game.gameField.setRobberHex( hex.get )
      newGameField.adjacentPlayers( hex.get ).filter( _ != game.onTurn ) match {
        case Nil => success(
          game.setState( state.nextState )
            .setGameField( newGameField )
        )
        case List( stealPlayerID ) => steal( game, stealPlayerID, state.nextState, Some( newGameField ) )
        case adjacentPlayers => success(
          game.setState( RobberStealState( adjacentPlayers, state.nextState ) )
            .setGameField( newGameField )
        )
      }
    }
  }

  override def undoStep( game:Game ):Game = {
    val h = game.gameField.findHex( hID ).get
    val newGameField = if( actualRobber.isDefined ) game.gameField.setRobberHex( actualRobber.get ) else game.gameField
    game.gameField.adjacentPlayers( h ).filter( _ != game.onTurn ) match {
      case List( stealPlayerID ) if robbedResource.isDefined =>
        game.setState( state )
          .setGameField( newGameField )
          .updatePlayers(
            game.player.removeResourceCard( robbedResource.get ).get,
            game.players( stealPlayerID ).addResourceCard( robbedResource.get )
          )
      case _ => game.setState( state )
        .setGameField( newGameField )
    }
  }

  /*override def toString:String = getClass.getSimpleName + ": hID[" + hID + "], actualRobber[" + actualRobber.useOrElse( _.id, -1 ) +
    "], robbedResources[" + robbedResource.useOrElse( r => r, "-" ) + "], " + state*/
}
