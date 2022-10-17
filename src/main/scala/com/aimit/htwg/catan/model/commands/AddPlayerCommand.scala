package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.state.{ InitBeginnerState, InitPlayerState }
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object AddPlayerCommand extends CommandImpl( "AddPlayerCommand" ) {
  override def fromXML( node:Node ):AddPlayerCommand = AddPlayerCommand(
    playerColor = PlayerColor.of( ( node \ "@playerColor" ).content ).get,
    name = ( node \ "@name" ).content,
    state = InitPlayerState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):AddPlayerCommand = AddPlayerCommand(
    playerColor = ( json \ "playerColor" ).as[PlayerColor],
    name = ( json \ "name" ).as[String],
    state = InitPlayerState.fromJson( ( json \ "state" ).get )
  )
}

case class AddPlayerCommand( playerColor:PlayerColor, name:String, state:InitPlayerState ) extends Command {

  def toXML:Node = <AddPlayerCommand playerColor={ playerColor.title } name={ name }>
    <state>{ state.toXML }</state>
  </AddPlayerCommand>.copy( label = AddPlayerCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( AddPlayerCommand.name ),
    "playerColor" -> Json.toJson( playerColor ),
    "name" -> Json.toJson( name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( name.isEmpty )
      Failure( PlayerNameEmpty )
    else if( name.length > game.maxPlayerNameLength )
      Failure( PlayerNameTooLong( name ) )
    else if ( game.players.exists( _._2.name ^= name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else if ( game.players.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else {
      val newGame = game.addPlayer( playerColor, name )
      if ( newGame.players.size >= game.maxPlayers )
        success( newGame.setState( InitBeginnerState() ) )
      else
        success( newGame )
    }
  }

  override def undoStep( game:Game ):Game = game.removeLastPlayer().setState( state )

  //override def toString:String = getClass.getSimpleName + ": Name[" + name + "], Color[" + playerColor.name + "]"
}
