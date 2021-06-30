package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State, StateImpl }
import de.htwg.se.catan.model.commands.RobberStealCommand
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLSequence }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberStealState extends StateImpl( "RobberStealState" ) {
  def fromXML( node:Node ):RobberStealState = RobberStealState(
    adjacentPlayers = node.childOf( "adjacentPlayers" ).asList( n => PlayerID.fromXML( n ) ),
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )

  def fromJson( json:JsValue ):RobberStealState = RobberStealState(
    adjacentPlayers = ( json \ "adjacentPlayers" ).asList[PlayerID],
    nextState = ( json \ "nextState" ).as[State]
  )
}

case class RobberStealState( adjacentPlayers:List[PlayerID],
                             nextState:State ) extends State {

  def toXML:Node = <RobberStealState>
    <adjacentPlayers>{ adjacentPlayers.toXML( _.toXML ) }</adjacentPlayers>
    <nextState>{ nextState.toXML }</nextState>
  </RobberStealState>.copy( label = RobberStealState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RobberStealState.name ),
    "adjacentPlayers" -> Json.toJson( adjacentPlayers ),
    "nextState" -> Json.toJson( nextState )
  )

  override def robberStealFromPlayer( stealPlayerID:PlayerID ):Option[Command] = Some(
    RobberStealCommand( stealPlayerID, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": adjacentPlayers[" + adjacentPlayers.mkString( ", " ) +
    "], NextState[" + nextState + "]"*/
}
