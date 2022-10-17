package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.{ Command, State, StateImpl, StructurePlacement }
import com.aimit.htwg.catan.model.commands.BuildCommand
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildState extends StateImpl( "BuildState" ) {
  def fromXML( node:Node ):BuildState = BuildState(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get
  )

  def fromJson( json:JsValue ):BuildState = BuildState(
    structure = ( json \ "structure" ).as[StructurePlacement]
  )
}

case class BuildState( structure:StructurePlacement ) extends State {

  def toXML:Node = <BuildState structure={ structure.title } />.copy( label = BuildState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuildState.name ),
    "structure" -> Json.toJson( structure )
  )

  override def build( id:Int ):Option[Command] = Some(
    BuildCommand( id, this )
  )

  //override def toString:String = getClass.getSimpleName + ": StructurePlacement[" + structure.title + "]"
}
