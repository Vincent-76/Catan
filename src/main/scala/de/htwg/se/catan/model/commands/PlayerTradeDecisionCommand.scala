package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.catan.model.{ Command, CommandImpl, Game }
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeDecisionCommand extends CommandImpl( "PlayerTradeDecisionCommand" ) {
  override def fromXML( node:Node ):PlayerTradeDecisionCommand = PlayerTradeDecisionCommand(
    decision = ( node \ "@decision" ).content.toBoolean,
    state = PlayerTradeState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):PlayerTradeDecisionCommand = PlayerTradeDecisionCommand(
    decision = ( json \ "decision" ).as[Boolean],
    state = PlayerTradeState.fromJson( ( json \ "state" ).get )
  )
}

case class PlayerTradeDecisionCommand( decision:Boolean, state:PlayerTradeState ) extends Command {

  def toXML:Node = <PlayerTradeDecisionCommand decision={ decision.toString }>
    <state>{ state.toXML }</state>
  </PlayerTradeDecisionCommand>.copy( label = PlayerTradeDecisionCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerTradeDecisionCommand.name ),
    "decision" -> Json.toJson( decision ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val decisions = state.decisions.updated( state.pID, decision )
    success( game.getNextTradePlayerInOrder( decisions, state.pID ) match {
      case Some( pID ) => game.setState( PlayerTradeState( pID, state.give, state.get, decisions ) )
      case None => game.setState( PlayerTradeEndState( state.give, state.get, decisions ) )
    }, None )
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": Decision[" + decision + "], " + state
}
