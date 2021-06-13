package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.state.MonopolyState
import de.htwg.se.settlers.model.{ Player, _ }
import de.htwg.se.settlers.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
case class MonopolyCommand( r:Resource, state:MonopolyState ) extends Command {

  var robbedResources:Option[Map[PlayerID, Int]] = Option.empty

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val newData = game.players.red( (List.empty, Map.empty[PlayerID, Int]),
      ( data:(List[Player], Map[PlayerID, Int]), pID:PlayerID, p:Player ) => {
        if( pID != game.onTurn ) {
          val amount = p.resourceAmount( r )
          (data._1 :+ p.removeResourceCard( r, amount ).get, data._2.updated( pID, amount ))
        } else data
      } )
    robbedResources = Some( newData._2 )
    val amount = newData._2.red( 0, ( i:Int, _:PlayerID, a:Int ) => i + a )
    success(
      game.setState( state.nextState )
        .updatePlayers( newData._1:_* ),
      info = Some( ResourceChangeInfo(
        playerAdd = Map( game.onTurn -> ResourceCards.ofResource( r, amount ) ),
        playerSub = newData._2.map( d => (d._1, ResourceCards.ofResource( r, d._2 )) )
      ) )
    )
  }

  override def undoStep( game:Game ):Game = (robbedResources match {
    case None => game
    case Some( playerResources ) =>
      val amount = playerResources.red( 0, ( i:Int, _:PlayerID, a:Int ) => i + a )
      game.updatePlayers( playerResources.map( d => game.player( d._1 ).addResourceCard( r, d._2 ) ).toSeq:_* )
  }).setState( state )

  /*override def toString:String = getClass.getSimpleName + ": Resource[" + r + "], " + state +
    ", RobbedResources[" + robbedResources.useOrElse( r => r.map( d => d._1 + ": " + d._2 ).mkString( ", " ), "-" ) + "]"*/
}
