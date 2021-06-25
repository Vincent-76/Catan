package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State, StateImpl }
import de.htwg.se.catan.model.commands.BuildInitSettlementCommand
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildInitSettlementState extends StateImpl( "BuildInitSettlementState" ) {
  def fromXML( node:Node ):BuildInitSettlementState = BuildInitSettlementState()

  def fromJson( json:JsValue ):State = BuildInitSettlementState()
}

case class BuildInitSettlementState() extends State {

  def toXML:Node = <BuildInitSettlementState />.copy( label = BuildInitSettlementState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuildInitSettlementState.name )
  )

  override def buildInitSettlement( vID:Int ):Option[Command] = Some(
    BuildInitSettlementCommand( vID, this )
  )

  //override def toString:String = getClass.getSimpleName
}