package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuyDevCardCommand extends CommandImpl( "BuyDevCardCommand" ) {
  override def fromXML( node:Node ):BuyDevCardCommand = BuyDevCardCommand(
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):BuyDevCardCommand = BuyDevCardCommand(
    state = ( json \ "state" ).as[State]
  )
}

case class BuyDevCardCommand( state:State ) extends Command {

  def toXML:Node = <BuyDevCardCommand>
    <state>{ state.toXML }</state>
  </BuyDevCardCommand>.copy( label = BuyDevCardCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuyDevCardCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] = game.drawDevCard( game.onTurn ) match {
    case Failure( t ) => Failure( t )
    case Success( newGame ) => Success( newGame, Some( DrawnDevCardInfo( newGame.onTurn, newGame.turn.getLastDrawnDevCard.get ) ) )
  }

  override def undoStep( game:Game ):Game = {
    val newGame = game.drawResourceCards( game.onTurn, DevelopmentCard.cardCost )._1
    val devCard = newGame.player.devCards.last
    newGame.setState( state )
      .setTurn( newGame.turn.removeDrawnDevCard() )
      .addDevCard( devCard )
      .updatePlayer( newGame.player.removeLastDevCard() )
  }

  //override def toString:String = getClass.getSimpleName + ": " + state
}
