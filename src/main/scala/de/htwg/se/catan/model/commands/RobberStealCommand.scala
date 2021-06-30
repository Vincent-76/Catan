package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model._
import de.htwg.se.catan.model.state.RobberStealState
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberStealCommand extends CommandImpl( "RobberStealCommand" ) {
  override def fromXML( node:Node ):RobberStealCommand = ???

  override def fromJson( json:JsValue ):RobberStealCommand = ???
}

case class RobberStealCommand( stealPlayerID:PlayerID, state:RobberStealState ) extends RobberCommand {

  def toXML:Node = <RobberStealCommand>
    <state>{ state.toXML }</state>
  </RobberStealCommand>.copy( label = RobberStealCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RobberStealCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.playerHasAdjacentVertex( stealPlayerID, game.gameField.adjacentVertices( game.gameField.robberHex ) ) )
      Failure( NoAdjacentStructure )
    else steal( game, stealPlayerID, state.nextState )
  }

  override def undoStep( game:Game ):Game = robbedResource match {
    case Some( r ) => game.setState( state )
      .updatePlayers(
        game.player.removeResourceCard( r ).get,
        game.players( stealPlayerID ).addResourceCard( r )
      )
    case None => game.setState( state )
  }

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) +  "], stealPlayerID[" + stealPlayerID + "], " + state
}
