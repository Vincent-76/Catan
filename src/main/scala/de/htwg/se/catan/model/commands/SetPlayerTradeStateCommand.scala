package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode }
import de.htwg.se.catan.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, InsufficientResources, State }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetPlayerTradeStateCommand extends CommandImpl( "SetPlayerTradeStateCommand" ) {
  override def fromXML( node:Node ):SetPlayerTradeStateCommand = SetPlayerTradeStateCommand(
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):SetPlayerTradeStateCommand = SetPlayerTradeStateCommand(
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards],
    state = ( json \ "state" ).as[State]
  )
}

case class SetPlayerTradeStateCommand( give:ResourceCards, get:ResourceCards, state:State ) extends Command {

  def toXML:Node = <SetPlayerTradeStateCommand>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <get>{ get.toXML( _.title, _.toString ) }</get>
    <state>{ state.toXML }</state>
  </SetPlayerTradeStateCommand>.copy( label = SetPlayerTradeStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetPlayerTradeStateCommand.name ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.player.hasResources( give ) )
      Failure( InsufficientResources )
    else {
      val decisions = game.players.values.filter( p => p != game.player && !p.hasResources( get ) ).map( p => (p.id, false) ).toMap
      val nextState = game.getNextTradePlayerInOrder( decisions ) match {
        case Some( pID ) => PlayerTradeState( pID, give, get, decisions )
        case None => PlayerTradeEndState( give, get, decisions )
      }
      success( game.setState( nextState ) )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": give[" + give + "], get[" + get + "], " + state
}
