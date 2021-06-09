package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.cards.Cards._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BankTradeCommand( give:ResourceCards, get:ResourceCards ) extends Command {

  var giveResources:Option[ResourceCards] = None

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    val (maxGetAmount, factors) = give.red( (0, Map.empty[Resource, Int]), ( data:(Int, Map[Resource, Int]), r:Resource, i:Int ) => {
      val factor = game.getBankTradeFactor( game.onTurn, r )
      (data._1 + i / factor, data._2.updated( r, factor ))
    } )
    if ( !game.player.resources.has( give ) )
      Failure( InsufficientResources )
    else if ( maxGetAmount < 0 || maxGetAmount < get.amount )
      Failure( InsufficientResources )
    else if ( !game.resourceStack.has( get ) )
      Failure( InsufficientBankResources )
    else {
      val (giveResources, _) = give.red( (give, 0), (data:(ResourceCards, Int), r:Resource, amount:Int ) => {
        if( data._2 < get.amount ) {
          val rAmount = amount / factors( r )
          val a = if ( data._2 + rAmount > get.amount )
            get.amount - data._2
          else rAmount
          (data._1.updated( r, a * factors( r ) ), data._2 + a)
        } else (data._1.updated( r, 0 ), data._2)
      } )
      this.giveResources = Some( giveResources )
      game.drawResourceCards( game.onTurn, get ).dropResourceCards( game.onTurn, giveResources ) match {
        case Success( newGame ) => Success(
          newGame,
          Some( BankTradedInfo( game.onTurn, giveResources, get ) ) )
        //case f => f.rethrow
      }
    }
  }

  override def undoStep( game:Game ):Game = giveResources match {
    case Some( given ) =>
      game.drawResourceCards( game.onTurn, given ).dropResourceCards( game.onTurn, get ).get
    case None => game
  }

  //override def toString:String = getClass.getSimpleName + ": Give: " + give + " | Get: " + get + " | GiveResources: " + giveResources.useOrElse( r => r, "-" )
}
