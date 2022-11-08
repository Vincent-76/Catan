package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object BankTradeCommand extends CommandImpl( "BankTradeCommand" ) {
  override def fromXML( node:Node ):BankTradeCommand = BankTradeCommand(
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) )
  )

  override def fromJson( json:JsValue ):BankTradeCommand = BankTradeCommand(
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards]
  )
}

case class BankTradeCommand( give:ResourceCards, get:ResourceCards ) extends Command {

  def toXML:Node = <BankTradeCommand>
    <give>{ give.toXML( _.name, _.toString ) }</give>
    <get>{ get.toXML( _.name, _.toString ) }</get>
  </BankTradeCommand>.copy( label = BankTradeCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BankTradeCommand.name ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get )
  )

  var giveResources:Option[ResourceCards] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val (maxGetAmount, factors) = give.red( (0, Map.empty[Resource, Int]), ( data:(Int, Map[Resource, Int]), r:Resource, i:Int ) => {
      val factor = game.getBankTradeFactor( game.onTurn, r )
      (data._1 + i / factor, data._2.updated( r, factor ))
    } )
    if( !game.player.hasResources( give ) )
      Failure( InsufficientResources )
    else if( maxGetAmount < 0 || maxGetAmount < get.amount )
      Failure( InsufficientResources )
    else if( !game.hasStackResources( get ) )
      Failure( InsufficientBankResources )
    else {
      val (giveResources, _) = give.red( (give, 0), ( data:(ResourceCards, Int), r:Resource, amount:Int ) => {
        if( data._2 < get.amount ) {
          val rAmount = amount / factors( r )
          val a = if( data._2 + rAmount > get.amount )
            get.amount - data._2
          else rAmount
          (data._1.updated( r, a * factors( r ) ), data._2 + a)
        } else (data._1.updated( r, 0 ), data._2)
      } )
      this.giveResources = Some( giveResources )
      game.drawResourceCards( game.onTurn, get )._1.dropResourceCards( game.onTurn, giveResources ) match {
        case Success( newGame ) => success(
          newGame,
          Some( BankTradedInfo( game.onTurn, giveResources, get ) ) )
        //case f => f.rethrow
      }
    }
  }

  override def undoStep( game:Game ):Game = giveResources match {
    case Some( given ) =>
      game.drawResourceCards( game.onTurn, given )._1.dropResourceCards( game.onTurn, get ).get
    case None => game
  }

  //override def toString:String = getClass.getSimpleName + ": Give: " + give + " | Get: " + get + " | GiveResources: " + giveResources.useOrElse( r => r, "-" )
}
