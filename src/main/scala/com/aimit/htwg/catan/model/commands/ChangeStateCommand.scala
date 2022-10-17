package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLNode
import com.aimit.htwg.catan.model.{ Command, CommandImpl, Game, State }
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object ChangeStateCommand extends CommandImpl( "ChangeStateCommand" ) {
  override def fromXML( node:Node ):ChangeStateCommand = ChangeStateCommand(
    state = State.fromXML( node.childOf( "state" ) ),
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )

  override def fromJson( json:JsValue ):ChangeStateCommand = ChangeStateCommand(
    state = ( json \ "state" ).as[State],
    nextState = ( json \ "nextState" ).as[State]
  )
}

case class ChangeStateCommand( state:State, nextState:State ) extends Command {

  def toXML:Node = <ChangeStateCommand>
    <state>{ state.toXML }</state>
    <nextState>{ nextState.toXML }</nextState>
  </ChangeStateCommand>.copy( label = ChangeStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ChangeStateCommand.name ),
    "state" -> state.toJson,
    "nextState" -> nextState.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = success( game.setState( nextState ) )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": State[" + state + "], NextState[" + nextState + "], Info[" + info.useOrElse( i => i.getClass.getSimpleName, "-" ) + "]"
}
