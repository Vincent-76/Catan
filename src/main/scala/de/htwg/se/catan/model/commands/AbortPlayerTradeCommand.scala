package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, State }
import de.htwg.se.catan.model.state.{ ActionState, PlayerTradeEndState }
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object AbortPlayerTradeCommand extends CommandImpl( "AbortPlayerTradeCommand" ) {
  override def fromXML( node:Node ):AbortPlayerTradeCommand = AbortPlayerTradeCommand(
    state = PlayerTradeEndState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):AbortPlayerTradeCommand = AbortPlayerTradeCommand(
    state = PlayerTradeEndState.fromJson( ( json \ "state" ).get )
  )
}

case class AbortPlayerTradeCommand( state:PlayerTradeEndState ) extends Command {

  def toXML:Node = <AbortPlayerTradeCommand>
    <state>{ state.toXML }</state>
  </AbortPlayerTradeCommand>.copy( label = AbortPlayerTradeCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( AbortPlayerTradeCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = success(
    game.setState( ActionState() )
  )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": " + state
}
