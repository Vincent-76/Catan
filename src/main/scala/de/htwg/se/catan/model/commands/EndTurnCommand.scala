package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.state.NextPlayerState
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, State, Turn }
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object EndTurnCommand extends CommandImpl( "EndTurnCommand" ):
  override def fromXML( node:Node ):EndTurnCommand = EndTurnCommand(
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):EndTurnCommand = EndTurnCommand(
    state = ( json \ "state" ).as[State]
  )


case class EndTurnCommand( state:State ) extends Command:

  def toXML:Node = <EndTurnCommand>
    <state>{ state.toXML }</state>
  </EndTurnCommand>.copy( label = EndTurnCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( EndTurnCommand.name ),
    "state" -> state.toJson
  )

  var turn:Option[Turn] = None

  override def doStep( game:Game ):Try[CommandSuccess] =
    turn = Some( game.turn )
    success( game.setState( NextPlayerState() ).nextRound() )
  
  override def undoStep( game:Game ):Game = game.setState( state )
    .previousRound( turn )

  //override def toString:String = getClass.getSimpleName + ": " + state + ", turn[" + turn.useOrElse( t => t.playerID, "-" ) + "]"
