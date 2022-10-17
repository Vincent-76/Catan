package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLNode
import com.aimit.htwg.catan.model.state.{ ActionState, PlayerTradeEndState }
import com.aimit.htwg.catan.model.{ Command, _ }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeCommand extends CommandImpl( "PlayerTradeCommand" ) {
  override def fromXML( node:Node ):PlayerTradeCommand = PlayerTradeCommand(
    tradePlayerID = PlayerID.fromXML( node.childOf( "tradePlayerID" ) ),
    state = PlayerTradeEndState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):PlayerTradeCommand = PlayerTradeCommand(
    tradePlayerID = ( json \ "tradePlayerID" ).as[PlayerID],
    state = PlayerTradeEndState.fromJson( ( json \ "state" ).get )
  )
}

case class PlayerTradeCommand( tradePlayerID:PlayerID, state:PlayerTradeEndState ) extends Command {

  def toXML:Node = <PlayerTradeCommand>
    <tradePlayerID>{ tradePlayerID.toXML }</tradePlayerID>
    <state>{ state.toXML }</state>
  </PlayerTradeCommand>.copy( label = PlayerTradeCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerTradeCommand.name ),
    "tradePlayerID" -> Json.toJson( tradePlayerID ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !state.decisions.getOrElse( tradePlayerID, false ) )
      Failure( InvalidPlayer( tradePlayerID ) )
    else game.player.trade( state.get, state.give ) match {
      case Failure( _ ) => Failure( InsufficientResources )
      case Success( newPlayer ) => game.player( tradePlayerID ).trade( state.give, state.get ) match {
        case Failure( _ ) => Failure( TradePlayerInsufficientResources )
        case Success( tradePlayer ) => success(
          game.setState( ActionState() )
            .updatePlayers( newPlayer, tradePlayer ),
          info = Some( ResourceChangeInfo(
            playerAdd = Map( newPlayer.id -> state.get, tradePlayer.id -> state.give ),
            playerSub = Map( newPlayer.id -> state.give, tradePlayer.id -> state.get )
          ) ) )
      }
    }
  }

  override def undoStep( game:Game ):Game = {
    val newPlayer = game.player.trade( state.give, state.get ).get
    val tradePlayer = game.player( tradePlayerID ).trade( state.get, state.give ).get
    game.setState( state )
      .updatePlayers( newPlayer, tradePlayer )
  }

  //override def toString:String = getClass.getSimpleName + ": tradePlayerID[" + tradePlayerID + "], " + state
}
