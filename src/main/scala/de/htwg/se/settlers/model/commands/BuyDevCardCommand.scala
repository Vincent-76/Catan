package de.htwg.se.settlers.model.commands
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Cards, Command, DrawnDevCardInfo, Game, Info, State }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BuyDevCardCommand( state:State ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = game.drawDevCard( game.onTurn ) match {
    case Failure( t ) => Failure( t )
    case Success( newGame ) => Success( newGame, Some( DrawnDevCardInfo( newGame.onTurn, newGame.turn.getLastDrawnDevCard.get ) ) )
  }

  override def undoStep( game:Game ):Game = {
    val newGame = game.drawResourceCards( game.onTurn, Cards.developmentCardCost )
    val devCard = newGame.player.devCards.last
    newGame.copy(
      state = state,
      players = newGame.updatePlayers( newGame.player.removeDevCard() ),
      turn = newGame.turn.removeDrawnDevCard(),
      developmentCards = devCard +: newGame.developmentCards
    )
  }
}
