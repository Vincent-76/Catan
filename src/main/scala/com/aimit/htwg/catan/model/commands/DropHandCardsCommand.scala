package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode }
import com.aimit.htwg.catan.model.state.{ ActionState, DropHandCardsState, RobberPlaceState }
import com.aimit.htwg.catan.model.{ Command, CommandImpl, Game, InvalidResourceAmount, LostResourcesInfo }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object DropHandCardsCommand extends CommandImpl( "DropHandCardsCommand" ) {
  override def fromXML( node:Node ):DropHandCardsCommand = DropHandCardsCommand(
    state = DropHandCardsState.fromXML( node.childOf( "state" ) ),
    cards = ResourceCards.fromXML( node.childOf( "cards" ) )
  )

  override def fromJson( json:JsValue ):DropHandCardsCommand = DropHandCardsCommand(
    state = DropHandCardsState.fromJson( ( json \ "state" ).get ),
    cards = ( json \ "cards" ).as[ResourceCards]
  )
}

case class DropHandCardsCommand( state:DropHandCardsState, cards:ResourceCards ) extends Command {

  def toXML:Node = <DropHandCardsCommand>
    <state>{ state.toXML }</state>
    <cards>{ cards.toXML( _.name, _.toString ) }</cards>
  </DropHandCardsCommand>.copy( label = DropHandCardsCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DropHandCardsCommand.name ),
    "state" -> state.toJson,
    "cards" -> Json.toJson( cards )
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if ( cards.amount != ( game.player( state.pID ).resourceAmount / 2 ) )
      Failure( InvalidResourceAmount( cards.amount ) )
    else game.dropResourceCards( state.pID, cards ) match {
      case Success( newGame ) =>
        val nextState = newGame.checkHandCardsInOrder( game.players( state.pID ), state.dropped :+ state.pID ) match {
          case Some( p ) => DropHandCardsState( p.id, state.dropped :+ state.pID )
          case None => RobberPlaceState( ActionState() )
        }
        success( newGame.setState( nextState ), Some( LostResourcesInfo( state.pID, cards ) ) )
      //case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state ).drawResourceCards( state.pID, cards )._1

  //override def toString:String = getClass.getSimpleName + ": " + state + ", cards[" + cards + "]"
}
