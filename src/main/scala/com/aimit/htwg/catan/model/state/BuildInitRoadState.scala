package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.{ Command, State, StateImpl }
import com.aimit.htwg.catan.model.commands.BuildInitRoadCommand
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildInitRoadState extends StateImpl( "BuildInitRoadState" ) {
  def fromXML( node:Node ):BuildInitRoadState = BuildInitRoadState(
    settlementVID = ( node \ "@settlementVID" ).content.toInt
  )

  def fromJson( json:JsValue ):BuildInitRoadState = BuildInitRoadState(
    settlementVID = ( json \ "settlementVID" ).as[Int]
  )
}

case class BuildInitRoadState( settlementVID:Int ) extends State {

  def toXML:Node = <BuildInitRoadState settlementVID={ settlementVID.toString } />.copy( label = BuildInitRoadState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuildInitRoadState.name ),
    "settlementVID" -> Json.toJson( settlementVID )
  )

  override def buildInitRoad( eID:Int ):Option[Command] = Some(
    BuildInitRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": SettlementVID[" + settlementVID + "]"
}
