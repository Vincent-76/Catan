package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.{ Command, PlayerID, State, StateImpl }
import com.aimit.htwg.catan.model.commands.DropHandCardsCommand
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLSequence }
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

  def fromJson( json:JsValue ):DropHandCardsState = DropHandCardsState(
    pID = ( json \ "pID" ).as[PlayerID],
    dropped = ( json \ "dropped" ).asList[PlayerID]
  )
}

case class DropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State {

  def toXML:Node = <DropHandCardsState>
    <pID>{ pID.toXML }</pID>
    <dropped>{ dropped.toXML( _.toXML ) }</dropped>
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
