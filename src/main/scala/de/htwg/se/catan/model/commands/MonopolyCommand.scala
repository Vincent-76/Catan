package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.state.MonopolyState
import de.htwg.se.catan.model.{ Player, _ }
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object MonopolyCommand extends CommandImpl( "MonopolyCommand" ) {
  override def fromXML( node:Node ):MonopolyCommand = ???

  override def fromJson( json:JsValue ):MonopolyCommand = ???
}

case class MonopolyCommand( r:Resource, state:MonopolyState ) extends Command {

  def toXML:Node = <MonopolyCommand>
    <state>{ state.toXML }</state>
  </MonopolyCommand>.copy( label = MonopolyCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( MonopolyCommand.name ),
    "state" -> state.toJson
  )

  var robbedResources:Option[Map[PlayerID, Int]] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val newData = game.players.red( (List.empty, Map.empty[PlayerID, Int]),
      ( data:(List[Player], Map[PlayerID, Int]), pID:PlayerID, p:Player ) => {
        if( pID != game.onTurn ) {
          val amount = p.resourceAmount( r )
          (data._1 :+ p.removeResourceCard( r, amount ).get, data._2.updated( pID, amount ))
        } else data
      } )
    robbedResources = Some( newData._2 )
    val amount = newData._2.values.sum
    success(
      game.setState( state.nextState )
        .updatePlayers( ( newData._1 :+ game.player.addResourceCard( r, amount ) ):_* ),
      info = Some( ResourceChangeInfo(
        playerAdd = Map( game.onTurn -> ResourceCards.ofResource( r, amount ) ),
        playerSub = newData._2.map( d => (d._1, ResourceCards.ofResource( r, d._2 )) )
      ) )
    )
  }

  override def undoStep( game:Game ):Game = (robbedResources match {
    case None => game
    case Some( playerResources ) =>
      val amount = Math.min( playerResources.values.sum, game.player.resourceAmount( r ) )
      val nPlayer = game.player.removeResourceCard( r, amount ).get
      game.updatePlayers( ( playerResources.map( d => game.player( d._1 ).addResourceCard( r, d._2 ) ).toSeq :+ nPlayer ):_* )
  }).setState( state )

  /*override def toString:String = getClass.getSimpleName + ": Resource[" + r + "], " + state +
    ", RobbedResources[" + robbedResources.useOrElse( r => r.map( d => d._1 + ": " + d._2 ).mkString( ", " ), "-" ) + "]"*/
}
