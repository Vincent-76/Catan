package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State, StateImpl }
import de.htwg.se.catan.model.commands.PlaceRobberCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberPlaceState extends StateImpl( "RobberPlaceState" ):
  def fromXML( node:Node ):RobberPlaceState = RobberPlaceState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )

  def fromJson( json:JsValue ):RobberPlaceState = RobberPlaceState(
    nextState = ( json \ "nextState" ).as[State]
  )


case class RobberPlaceState( nextState:State ) extends State:

  def toXML:Node = <RobberPlaceState>
    <nextState>{ nextState.toXML }</nextState>
  </RobberPlaceState>.copy( label = RobberPlaceState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RobberPlaceState.name ),
    "nextState" -> Json.toJson( nextState )
  )

  override def placeRobber( hID:Int ):Option[Command] = Some(
    PlaceRobberCommand( hID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"

