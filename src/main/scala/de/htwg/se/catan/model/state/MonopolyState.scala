package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, Resource, State, StateImpl }
import de.htwg.se.catan.model.commands.MonopolyCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object MonopolyState extends StateImpl( "MonopolyState" ):
  def fromXML( node:Node ):MonopolyState = MonopolyState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )

  def fromJson( json:JsValue ):MonopolyState = MonopolyState(
    nextState = ( json \ "nextState" ).as[State]
  )


case class MonopolyState( nextState:State ) extends State:

  def toXML:Node = <MonopolyState>
    <nextState>{ nextState.toXML }</nextState>
  </MonopolyState>.copy( label = MonopolyState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( MonopolyState.name ),
    "nextState" -> Json.toJson( nextState )
  )

  override def monopolyAction( r:Resource ):Option[Command] = Some(
    MonopolyCommand( r, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
