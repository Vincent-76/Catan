package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.catan.model.{ Command, CommandImpl, Game, InsufficientResources, State }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object SetPlayerTradeStateCommand extends CommandImpl( "SetPlayerTradeStateCommand" ) {
  override def fromXML( node:Node ):SetPlayerTradeStateCommand = ???

  override def fromJson( json:JsValue ):SetPlayerTradeStateCommand = ???
}

case class SetPlayerTradeStateCommand( give:ResourceCards, get:ResourceCards, state:State ) extends Command {

  def toXML:Node = <SetPlayerTradeStateCommand>
    <state>{ state.toXML }</state>
  </SetPlayerTradeStateCommand>.copy( label = SetPlayerTradeStateCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SetPlayerTradeStateCommand.name ),
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
