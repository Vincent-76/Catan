package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLNode
import com.aimit.htwg.catan.model.state.{ ActionState, DropHandCardsState, RobberPlaceState }
import com.aimit.htwg.catan.model.{ Card, _ }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object RollDicesCommand extends CommandImpl( "RollDicesCommand" ) {
  override def fromXML( node:Node ):RollDicesCommand = RollDicesCommand(
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):RollDicesCommand = RollDicesCommand(
    state = ( json \ "state" ).as[State]
  )
}

case class RollDicesCommand( state:State ) extends Command {

  def toXML:Node = <RollDicesCommand>
    <state>{ state.toXML }</state>
  </RollDicesCommand>.copy( label = RollDicesCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RollDicesCommand.name ),
    "state" -> state.toJson
  )

  private var availablePlayerResources:Option[Map[PlayerID, ResourceCards]] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val dices = game.rollDices()
    println( "Dices[" + dices._1 + " + " + dices._2 + " = " + (dices._1 + dices._2) + "]" )
    DiceValue.of( dices._1 + dices._2 ) match {
      //case None => Failure( Fail )
      case Some( Seven ) => game.checkHandCardsInOrder() match {
        case Some( p ) => Success( game.setState( DropHandCardsState( p.id ) ), Some( DiceInfo( dices ) ) )
        case None => Success( game.setState( RobberPlaceState( ActionState() ) ), Some( DiceInfo( dices ) ) )
      }
      case Some( n:DiceValue ) =>
        val playerResources = game.gameField.hexList.red( Map.empty[PlayerID, ResourceCards], ( resources:Map[PlayerID, ResourceCards], h:Hex ) => {
          if( h != game.gameField.robberHex ) h.area match {
            case r:ResourceArea if r.number == n =>
              game.gameField.adjacentVertices( h ).red( resources, ( resources:Map[PlayerID, ResourceCards], v:Vertex ) =>
                v.building match {
                  case Some( v:Settlement ) =>
                    resources.updated( v.owner, resources.getOrElse( v.owner, Card.getResourceCards( 0 ) ).add( r.resource ) )
                  case Some( c:City ) =>
                    resources.updated( c.owner, resources.getOrElse( c.owner, Card.getResourceCards( 0 ) ).add( r.resource, 2 ) )
                  case _ => resources
                } )
            case _ => resources
          }
          else resources
        } )
        val (newGame, drawn) = playerResources.red( (game, Map.empty[PlayerID, ResourceCards]), ( data:(Game, Map[PlayerID, ResourceCards]), pID:PlayerID, cards:ResourceCards ) => {
          val (newGame, drawnResources) = data._1.drawResourceCards( pID, cards )
          (newGame, data._2.updated( pID, drawnResources ))
        } )
        /*val (available, newStack) = playerResources.red( (playerResources, game.resourceStack),
          ( data:(Map[PlayerID, ResourceCards], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
            val (available, newStack) = game.getAvailableResourceCards( cards, data._2 )
            if ( available.amount > 0 )
              (data._1.updated( pID, available ), newStack)
            else
              (data._1 - pID, data._2)
          }
        )*/
        availablePlayerResources = if( drawn.isEmpty ) None else Some( drawn )
        success(
          newGame.setState( ActionState() ),
          info = Some( GatherInfo( dices, playerResources ) )
        )
    }
  }

  override def undoStep( game:Game ):Game = availablePlayerResources match {
    case Some( available ) =>
      val newGame = available.red( game, ( g:Game, pID:PlayerID, cards:ResourceCards ) => {
        val p = g.player( pID )
        val cardsRemove = cards.map( d => (d._1, math.min( d._2, p.resourceAmount( d._1 ) )) )
        g.dropResourceCards( pID, cardsRemove ) match {
          case Success( newGame ) => newGame
          case _ => g
        }
      } )
      /*val (newPlayers, newStack) = available.red( (game.players, game.resourceStack),
        ( data:(SortedMap[PlayerID, Player], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
          (data._1.updated( pID, data._1( pID ).removeResourceCards( cards ).get ), data._2.add( cards ))
        } )*/
      newGame.setState( state )
    case None => game.setState( state )
  }

  /*override def toString:String = getClass.getSimpleName + ": availablePlayerResources[" +
    availablePlayerResources.useOrElse( d => d.map( d => d._1 + "[" + d._2 + "]" ).mkString( ", " ), "-" ) +
    "], " + state*/
}
