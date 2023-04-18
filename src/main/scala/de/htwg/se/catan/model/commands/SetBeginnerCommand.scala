package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.error.NoUniqueBeginner
import de.htwg.se.catan.model.state.{ BuildInitSettlementState, InitBeginnerState }
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, Turn }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetBeginnerCommand extends CommandImpl( "SetBeginnerCommand" ):
  override def fromXML( node:Node ):SetBeginnerCommand = SetBeginnerCommand(
    state = InitBeginnerState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):SetBeginnerCommand = SetBeginnerCommand(
    state = InitBeginnerState.fromJson( ( json \ "state" ).get )
  )


case class SetBeginnerCommand( state:InitBeginnerState ) extends Command:

  def toXML:Node = <SetBeginnerCommand>
    <state>{ state.toXML }</state>
  </SetBeginnerCommand>.copy( label = SetBeginnerCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetBeginnerCommand.name ),
    "state" -> state.toJson
  )

  var oldTurn:Option[Turn] = None

  override def doStep( game:Game ):Try[CommandSuccess] =
    if state.beginner.isEmpty then
      Failure( NoUniqueBeginner )
    else
      oldTurn = Some( game.turn )
      success( game.setState( BuildInitSettlementState() )
        .setTurn( game.turn.set( state.beginner.get ) )
      )

  override def undoStep( game:Game ):Game = game.setState( state )
    .setTurn( oldTurn.getOrElse( game.turn ) )

  //override def toString:String = getClass.getSimpleName + ": " + state
