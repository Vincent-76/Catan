package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Cards._
import de.htwg.se.catan.model.state.{ ActionState, DropHandCardsState, RobberPlaceState }
import de.htwg.se.catan.model.{ Command, Game, InvalidResourceAmount, LostResourcesInfo }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class DropHandCardsCommand( state:DropHandCardsState, cards:ResourceCards ) extends Command {

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
