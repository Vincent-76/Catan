package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BuyDevCardCommand( state:State ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = game.drawDevCard( game.onTurn ) match {
    case Failure( t ) => Failure( t )
    case Success( newGame ) => Success( newGame, Some( DrawnDevCardInfo( newGame.onTurn, newGame.turn.getLastDrawnDevCard.get ) ) )
  }

  override def undoStep( game:Game ):Game = {
    val newGame = game.drawResourceCards( game.onTurn, Cards.developmentCardCost )._1
    val devCard = newGame.player.devCards.last
    newGame.setState( state )
      .setTurn( newGame.turn.removeDrawnDevCard() )
      .addDevCard( devCard )
      .updatePlayer( newGame.player.removeLastDevCard() )
  }

  //override def toString:String = getClass.getSimpleName + ": " + state
}
