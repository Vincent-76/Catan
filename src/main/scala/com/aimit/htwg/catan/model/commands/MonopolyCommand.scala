package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import com.aimit.htwg.catan.model.state.MonopolyState
import com.aimit.htwg.catan.model.{ Player, _ }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object MonopolyCommand extends CommandImpl( "MonopolyCommand" ) {
  override def fromXML( node:Node ):MonopolyCommand = MonopolyCommand(
    resource = Resource.of( ( node \ "@resource" ).content ).get,
    state = MonopolyState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):MonopolyCommand = MonopolyCommand(
    resource = ( json \ "resource" ).as[Resource],
    state = MonopolyState.fromJson( ( json \ "state" ).get )
  )
}

case class MonopolyCommand( resource:Resource, state:MonopolyState ) extends Command {

  def toXML:Node = <MonopolyCommand resource={ resource.name }>
    <state>{ state.toXML }</state>
  </MonopolyCommand>.copy( label = MonopolyCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( MonopolyCommand.name ),
    "resource" -> Json.toJson( resource ),
    "state" -> state.toJson
  )

  var robbedResources:Option[Map[PlayerID, Int]] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val newData = game.players.red( (List.empty, Map.empty[PlayerID, Int]),
      ( data:(List[Player], Map[PlayerID, Int]), pID:PlayerID, p:Player ) => {
        if( pID != game.onTurn ) {
          val amount = p.resourceAmount( resource )
          (data._1 :+ p.removeResourceCard( resource, amount ).get, data._2.updated( pID, amount ))
        } else data
      } )
    robbedResources = Some( newData._2 )
    val amount = newData._2.values.sum
    success(
      game.setState( state.nextState )
        .updatePlayers( ( newData._1 :+ game.player.addResourceCard( resource, amount ) ):_* ),
      info = Some( ResourceChangeInfo(
        playerAdd = Map( game.onTurn -> ResourceCards.ofResource( resource, amount ) ),
        playerSub = newData._2.map( d => (d._1, ResourceCards.ofResource( resource, d._2 )) )
      ) )
    )
  }

  override def undoStep( game:Game ):Game = (robbedResources match {
    case None => game
    case Some( playerResources ) =>
      val amount = Math.min( playerResources.values.sum, game.player.resourceAmount( resource ) )
      val nPlayer = game.player.removeResourceCard( resource, amount ).get
      game.updatePlayers( ( playerResources.map( d => game.player( d._1 ).addResourceCard( resource, d._2 ) ).toSeq :+ nPlayer ):_* )
  }).setState( state )

  /*override def toString:String = getClass.getSimpleName + ": Resource[" + r + "], " + state +
    ", RobbedResources[" + robbedResources.useOrElse( r => r.map( d => d._1 + ": " + d._2 ).mkString( ", " ), "-" ) + "]"*/
}
