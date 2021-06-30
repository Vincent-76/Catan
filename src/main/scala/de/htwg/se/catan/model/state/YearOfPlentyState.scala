package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ Command, State, StateImpl }
import de.htwg.se.catan.model.commands.YearOfPlentyCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object YearOfPlentyState extends StateImpl( "YearOfPlentyState" ) {
  def fromXML( node:Node ):YearOfPlentyState = YearOfPlentyState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )

  def fromJson( json:JsValue ):YearOfPlentyState = YearOfPlentyState(
    nextState = ( json \ "nextState" ).as[State]
  )
}

case class YearOfPlentyState( nextState:State ) extends State {

  def toXML:Node = <YearOfPlentyState>
    <nextState>{ nextState.toXML }</nextState>
  </YearOfPlentyState>.copy( label = YearOfPlentyState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( YearOfPlentyState.name ),
    "nextState" -> Json.toJson( nextState )
  )

  override def yearOfPlentyAction( resources:ResourceCards ):Option[Command] = Some(
    YearOfPlentyCommand( resources, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
