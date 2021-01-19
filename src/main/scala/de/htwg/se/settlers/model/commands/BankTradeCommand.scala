package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BankTradeCommand( give:(Resource, Int), get:(Resource, Int) ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( give._1 == get._1 )
      return Failure( InvalidTradeResources( give._1, get._1 ) )
    val factor = game.getBankTradeFactor( game.onTurn, give._1 )
    val amount = give._2 / factor
    if ( amount < 0 || amount < get._2 )
      Failure( InsufficientResources )
    else if ( game.resourceStack.getOrElse( get._1, 0 ) < get._2 )
      Failure( InsufficientBankResources( get._1 ) )
    else game.drawResourceCards( game.onTurn, get._1, get._2 ).dropResourceCards( game.onTurn, give._1, get._2 * factor ) match {
      case Success( newGame ) => Success(
        newGame,
        Some( BankTradedInfo( game.onTurn, (give._1, get._2 * factor), (get._1, get._2) ) ) )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    val factor = game.getBankTradeFactor( game.onTurn, give._1 )
    game.drawResourceCards( game.onTurn, give._1, get._2 * factor ).dropResourceCards( game.onTurn, get._1, get._2 ).get
  }
}
