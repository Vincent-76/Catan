package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ Command, Game, Info, InvalidResourceAmount, LostResourcesInfo }
import de.htwg.se.settlers.model.state.{ ActionState, DropHandCardsState, RobberPlaceState }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class DropHandCardsCommand( state:DropHandCardsState, cards:ResourceCards ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( cards.amount != ( game.player( state.pID ).resources.amount / 2 ) )
      Failure( InvalidResourceAmount( cards.amount ) )
    else game.dropResourceCards( state.pID, cards ) match {
      case Success( newGame ) =>
        val nextState = newGame.checkHandCardsInOrder( game.players( state.pID ), state.dropped :+ state.pID ) match {
          case Some( p ) => DropHandCardsState( p.id, state.dropped :+ state.pID )
          case None => RobberPlaceState( ActionState() )
        }
        Success( newGame.setState( nextState ), Some( LostResourcesInfo( state.pID, cards ) ) )
      //case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = game.drawResourceCards( state.pID, cards ).setState( state )

  //override def toString:String = getClass.getSimpleName + ": " + state + ", cards[" + cards + "]"
}
