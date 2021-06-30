package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.state.YearOfPlentyState
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object YearOfPlentyCommand extends CommandImpl( "YearOfPlentyCommand" ) {
  override def fromXML( node:Node ):YearOfPlentyCommand = YearOfPlentyCommand(
    resources = ResourceCards.fromXML( node.childOf( "resources" ) ),
    state = YearOfPlentyState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):YearOfPlentyCommand = YearOfPlentyCommand(
    resources = ( json \ "resources" ).as[ResourceCards],
    state = YearOfPlentyState.fromJson( ( json \ "state" ).get )
  )
}

case class YearOfPlentyCommand( resources:ResourceCards, state:YearOfPlentyState ) extends Command {

  def toXML:Node = <YearOfPlentyCommand>
    <resources>{ resources.toXML( _.title, _.toString ) }</resources>
    <state>{ state.toXML }</state>
  </YearOfPlentyCommand>.copy( label = YearOfPlentyCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( YearOfPlentyCommand.name ),
    "resources" -> Json.toJson( resources ),
    "state" -> state.toJson
  )

  var drawnResources:Option[ResourceCards] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( resources.amount != 2 )
      Failure( InvalidResourceAmount( resources.amount ) )
    else {
      val (availableResources, _) = game.getAvailableResourceCards( resources )
      drawnResources = Some( availableResources )
      success(
        game.setState( state.nextState ).drawResourceCards( game.onTurn, availableResources )._1,
        info = Some( GotResourcesInfo( game.onTurn, availableResources ) )
      )
    }
  }

  override def undoStep( game:Game ):Game = (drawnResources match {
    case None => game
    case Some( available ) => game.dropResourceCards( game.onTurn, available ).get
  }).setState( state )

  //override def toString:String = getClass.getSimpleName + ": resources[" + resources + "], " + state
}
