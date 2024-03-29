package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.commands.InitGameCommand
import com.aimit.htwg.catan.model.{ Command, State, StateImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitState extends StateImpl( "InitState" ) {
  def fromXML( node:Node ):InitState = InitState()

  def fromJson( json:JsValue ):InitState = InitState()
}

case class InitState( ) extends State {

  def toXML:Node = <InitState />.copy( label = InitState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InitState.name )
  )

  override def initGame( ):Option[Command] = Some(
    InitGameCommand()
  )

  //override def toString:String = getClass.getSimpleName
}
