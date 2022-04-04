package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.Card.resourceCardsReads
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode }
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object BankTradeCommand extends CommandImpl( "BankTradeCommand" ):
  override def fromXML( node:Node ):BankTradeCommand = BankTradeCommand(
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) )
  )

  override def fromJson( json:JsValue ):BankTradeCommand = BankTradeCommand(
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards]
  )


case class BankTradeCommand( give:ResourceCards, get:ResourceCards ) extends Command:

  def toXML:Node = <BankTradeCommand>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <get>{ get.toXML( _.title, _.toString ) }</get>
  </BankTradeCommand>.copy( label = BankTradeCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BankTradeCommand.name ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get )
  )

  var giveResources:Option[ResourceCards] = None

  override def doStep( game:Game ):Try[CommandSuccess] =
    val (maxGetAmount, factors) = give.red( (0, Map.empty[Resource, Int]), ( data:(Int, Map[Resource, Int]), r:Resource, i:Int ) => {
      val factor = game.getBankTradeFactor( game.onTurn, r )
      (data._1 + i / factor, data._2.updated( r, factor ))
    } )
    if !game.player.hasResources( give ) then
      Failure( InsufficientResources )
    else if maxGetAmount < 0 || maxGetAmount < get.amount then
      Failure( InsufficientResources )
    else if !game.hasStackResources( get ) then
      Failure( InsufficientBankResources )
    else
      val (giveResources, _) = give.red( (give, 0), ( data:(ResourceCards, Int), r:Resource, amount:Int ) => {
        if data._2 < get.amount then
          val rAmount = amount / factors( r )
          val a = if data._2 + rAmount > get.amount then
            get.amount - data._2
          else rAmount
          (data._1.updated( r, a * factors( r ) ), data._2 + a)
        else (data._1.updated( r, 0 ), data._2)
      } )
      this.giveResources = Some( giveResources )
      game.drawResourceCards( game.onTurn, get )._1.dropResourceCards( game.onTurn, giveResources ) match
        case Success( newGame ) => success(
          newGame,
          Some( Info.BankTradedInfo( game.onTurn, giveResources, get ) ) )
        //case f => f.rethrow

  override def undoStep( game:Game ):Game = giveResources match
    case Some( resources ) =>
      game.drawResourceCards( game.onTurn, resources )._1.dropResourceCards( game.onTurn, get ).get
    case None => game

  //override def toString:String = getClass.getSimpleName + ": Give: " + give + " | Get: " + get + " | GiveResources: " + giveResources.useOrElse( r => r, "-" )
