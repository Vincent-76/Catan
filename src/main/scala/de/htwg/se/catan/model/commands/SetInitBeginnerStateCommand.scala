package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.state.InitBeginnerState
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, NotEnoughPlayers, State }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetInitBeginnerStateCommand extends CommandImpl( "SetInitBeginnerStateCommand" ) {
  override def fromXML( node:Node ):SetInitBeginnerStateCommand = ???

  override def fromJson( json:JsValue ):SetInitBeginnerStateCommand = ???
}

case class SetInitBeginnerStateCommand( state:State ) extends Command {

  def toXML:Node = <SetInitBeginnerStateCommand>
    <state>{ state.toXML }</state>
  </SetInitBeginnerStateCommand>.copy( label = SetInitBeginnerStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetInitBeginnerStateCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( game.players.size >= game.minPlayers )
      success( game.setState( InitBeginnerState() ) )
    else
      Failure( NotEnoughPlayers )
  }

  override def undoStep( game:Game ):Game = game.setState( state )
}
