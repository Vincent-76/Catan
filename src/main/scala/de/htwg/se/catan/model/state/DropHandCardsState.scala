package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ Command, PlayerID, State, StateImpl }
import de.htwg.se.catan.model.commands.DropHandCardsCommand
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DropHandCardsState extends StateImpl( "DropHandCardsState" ) {
  def fromXML( node:Node ):DropHandCardsState = DropHandCardsState(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    dropped = node.childOf( "dropped" ).asList( n => PlayerID.fromXML( n ) )
  )

  def fromJson( json:JsValue ):State = DropHandCardsState(
    pID = ( json \ "pID" ).as[PlayerID],
    dropped = ( json \ "dropped" ).asList[PlayerID]
  )
}

case class DropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State {

  def toXML:Node = <DropHandCardsState>
    <pID>{ pID.toXML }</pID>
    <dropped>{ dropped.map( pID => pID.toXML ) }</dropped>
  </DropHandCardsState>.copy( label = DropHandCardsState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DropHandCardsState.name ),
    "pID" -> Json.toJson( pID ),
    "dropped" -> Json.toJson( dropped )
  )

  override def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = Some(
    DropHandCardsCommand( this, cards )
  )

  //override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], dropped[" + dropped.mkString( ", " ) + "]"
}
