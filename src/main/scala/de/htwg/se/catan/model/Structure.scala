package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonSerializable, XMLDeserializer, XMLSerializable }
import play.api.libs.json._

import scala.xml.Node

abstract class StructureImpl[+T <: Structure]( name:String ) extends DeserializerComponentImpl[T]( name ) {
  override def init():Unit = Structure.addImpl( this )
}

object Structure extends ClassComponent[Structure, StructureImpl[Structure]] {
  implicit def structureWrites:Writes[Structure] = ( o:Structure ) => o.toJson
  implicit def structureReads:Reads[Structure] = ( json:JsValue ) => JsSuccess( fromJson( json ) )

  def init():Unit = {
    Road.init()
    Settlement.init()
    City.init()
  }
}

abstract class Structure( val owner:PlayerID ) extends XMLSerializable with JsonSerializable



object Road extends StructureImpl[Road]( "Road" ) {
  def fromXML( node:Node ):Road = Road( PlayerID.fromXML( node.childOf( "owner" ) ) )

  def fromJson( json:JsValue ):Road = Road( ( json \ "owner" ).as[PlayerID] )

  implicit def roadWrites:Writes[Road] = ( o:Road ) => o.toJson

  implicit def roadReads:Reads[Road] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
}

case class Road( override val owner:PlayerID ) extends Structure( owner ) {
  def toXML:Node = <Road>
    <owner>{ owner.toXML }</owner>
  </Road>.copy( label = Road.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( Road.name ),
    "owner" -> Json.toJson( owner )
  )
}



abstract class BuildingImpl[+T <: Building]( name:String ) extends StructureImpl[T]( name ) {
  override def init():Unit = {
    super.init()
    Building.addImpl( this )
  }
}

object Building extends ClassComponent[Building, BuildingImpl[Building]] {
  implicit def buildingWrites:Writes[Building] = ( o:Building ) => o.toJson
  implicit def buildingReads:Reads[Building] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
}

abstract class Building( owner:PlayerID ) extends Structure( owner )



object Settlement extends BuildingImpl[Settlement]( "Settlement" ) {
  def fromXML( node:Node ):Settlement = Settlement( PlayerID.fromXML( node.childOf( "owner" ) ) )

  def fromJson( json:JsValue ):Settlement = Settlement( ( json \ "owner" ).as[PlayerID] )
}

case class Settlement( override val owner:PlayerID ) extends Building( owner ) {
  def toXML:Node = <Settlement>
    <owner>{ owner.toXML }</owner>
  </Settlement>.copy( label = Settlement.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( Settlement.name ),
    "owner" -> Json.toJson( owner )
  )
}


object City extends BuildingImpl[City]( "City" ) {
  def fromXML( node:Node ):City = City( PlayerID.fromXML( node.childOf( "owner" ) ) )

  def fromJson( json:JsValue ):City = City( ( json \ "owner" ).as[PlayerID] )
}

case class City( override val owner:PlayerID ) extends Building( owner ) {
  def toXML:Node = <City>
    <owner>{ owner.toXML }</owner>
  </City>.copy( label = City.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( City.name ),
    "owner" -> Json.toJson( owner )
  )
}
