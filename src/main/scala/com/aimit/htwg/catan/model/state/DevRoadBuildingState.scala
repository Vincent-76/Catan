package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.{ Command, State, StateImpl }
import com.aimit.htwg.catan.model.commands.DevBuildRoadCommand
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DevRoadBuildingState extends StateImpl( "DevRoadBuildingState" ) {
  def fromXML( node:Node ):DevRoadBuildingState = DevRoadBuildingState(
    nextState = State.fromXML( node.childOf( "nextState" ) ),
    roads = ( node \ "@roads" ).content.toInt
  )

  def fromJson( json:JsValue ):DevRoadBuildingState = DevRoadBuildingState(
    nextState = ( json \ "nextState" ).as[State],
    roads = ( json \ "roads" ).as[Int]
  )
}

case class DevRoadBuildingState( nextState:State, roads:Int = 0 ) extends State {

  def toXML:Node = <DevRoadBuildingState roads={ roads.toString }>
    <nextState>{ nextState.toXML }</nextState>
  </DevRoadBuildingState>.copy( label = DevRoadBuildingState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DevRoadBuildingState.name ),
    "nextState" -> Json.toJson( nextState ),
    "roads" -> Json.toJson( roads )
  )

  override def devBuildRoad( eID:Int ):Option[Command] = Some(
    DevBuildRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": NextState[" + nextState + "], Roads[" + roads + "]"
}
