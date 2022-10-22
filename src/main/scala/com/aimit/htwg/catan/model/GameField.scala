package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.GameField.Field
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import com.aimit.htwg.catan.model.impl.fileio.{ JsonSerializable, XMLDeserializer, XMLSerializable }
import play.api.libs.json.{ JsSuccess, JsValue, Json, OWrites, Reads, Writes }

import scala.collection.immutable.List
import scala.xml.Node

abstract class GameFieldImpl( name:String ) extends DeserializerComponentImpl[GameField]( name ) {
  override def init():Unit = GameField.addImpl( this )
}

object GameField extends ClassComponent[GameField, GameFieldImpl] {

  type Field[E] = Vector[Row[E]]

  type Row[E] = Vector[Option[E]]

  type Edges = Map[(Hex, Hex), Edge]

  type Vertices = Map[(Hex, Hex, Hex), Vertex]

  /***
   * starting on the south western hex counterclockwise
   */
  val adjacentOffset = Vector( (1, -1), (1, 0), (0, 1), (-1, 1), (-1, 0), (0, -1) )

  implicit val turnWrites:Writes[GameField] = ( o:GameField ) => o.toJson

  implicit val turnReads:Reads[GameField] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
}

trait GameField extends XMLSerializable with JsonSerializable {

  def field:Field[Hex]

  def fieldWidth:Int
  def fieldHeight:Int
  def robberHex:Hex
  def setRobberHex( hex:Hex ):GameField

  // Hex

  def hexList:List[Hex]
  def findHex( hID:Int ):Option[Hex]
  def findHex( row:Int, col:Int ):Option[Hex]
  def adjacentHexes( h:Hex ):List[Hex]
  def adjacentHex( h:Hex, offsetIndex:Int ):Option[Hex]
  def adjacentEdges( h:Hex ):List[Edge]
  def adjacentEdge( h:Hex, offsetIndex:Int ):Option[Edge]
  def adjacentVertices( h:Hex ):List[Vertex]
  def adjacentPlayers( h:Hex ):List[PlayerID]


  // Edge

  def edgeList:List[Edge]
  def findEdge( eID:Int ):Option[Edge]
  def findEdge( h:(Hex, Hex) ):Option[Edge]
  def adjacentHexes( e:Edge ):List[Hex]
  def adjacentEdges( e:Edge ):List[Edge]
  def adjacentVertices( e:Edge ):List[Vertex]
  def update( e:Edge ):GameField


  // Vertex

  def vertexList:List[Vertex]
  def findVertex( vID:Int ):Option[Vertex]
  def findVertex( h:(Hex, Hex, Hex) ):Option[Vertex]
  def adjacentEdges( v:Vertex ):List[Edge]
  def update( v:Vertex ):GameField

}

sealed trait PlacementPoint

object Hex extends XMLDeserializer[Hex] {
  //private def apply( id:Int, r:Int, c:Int, area:Area ):Hex = new Hex( id, r, c, area )

  def fromXML( node:Node ):Hex = Hex(
    id = ( node \ "@id" ).content.toInt,
    row = ( node \ "@row" ).content.toInt,
    col = ( node \ "@col" ).content.toInt,
    area = Area.fromXML( node.childOf( "area" ) )
  )

  implicit val hexWrites:Writes[Hex] = Json.writes[Hex]
  implicit val hexReads:Reads[Hex] = Json.reads[Hex]
}

case class Hex /*private[GameField]*/( id:Int, row:Int, col:Int, area:Area ) extends PlacementPoint with XMLSerializable {
  //private def copy( ):Unit = {}

  def toXML:Node = <Hex id={ id.toString } row={ row.toString } col={ col.toString }>
    <area>{ area.toXML }</area>
  </Hex>

  def isWater:Boolean = area.f == Water

  def isLand:Boolean = area.isInstanceOf[LandArea]
}

object Edge {
  //private def apply( id:Int, h1:Hex, h2:Hex, port:Option[Port], building:Option[Road] ):Edge = new Edge( id, h1, h2, port, building )

  def fromXML( node:Node, hexList:List[Hex] ):Edge = Edge(
    id = ( node \ "@id" ).content.toInt,
    h1 = hexList.find( _.id == ( node \ "@h1" ).content.toInt ).get,
    h2 = hexList.find( _.id == ( node \ "@h2" ).content.toInt ).get,
    port = node.childOf( "port" ).asOption( n => Port.fromXML( n ) ),
    road = node.childOf( "road" ).asOption( n => Structure.fromXML( n ).asInstanceOf[Road] )
  )

  implicit val edgeWrites:Writes[Edge] = ( o:Edge ) => Json.obj(
    "id" -> Json.toJson( o.id ),
    "h1" -> Json.toJson( o.h1.id ),
    "h2" -> Json.toJson( o.h2.id ),
    "port" -> Json.toJson( o.port ),
    "road" -> Json.toJson( o.road )
  )

  def fromJson( json:JsValue, hexList:List[Hex] ):Edge = Edge(
    id = ( json \ "id" ).as[Int],
    h1 = hexList.find( _.id == ( json \ "h1" ).as[Int] ).get,
    h2 = hexList.find( _.id == ( json \ "h2" ).as[Int] ).get,
    port = ( json \ "port" ).asOption[Port],
    road = ( json \ "road" ).asOption[Road]
  )
}

case class Edge /*private[GameField]*/( id:Int, h1:Hex, h2:Hex, port:Option[Port] = None, road:Option[Road] = None )
  extends PlacementPoint with XMLSerializable {

  def toXML:Node = <Edge id={ id.toString } h1={ h1.id.toString } h2={ h2.id.toString }>
    <port>{ port.toXML( _.toXML ) }</port>
    <road>{ road.toXML( _.toXML ) }</road>
  </Edge>

  //private def copy( ):Unit = {}

  def setRoad( road:Option[Road] ):Edge = new Edge( id, h1, h2, port, road )
}

object Vertex {
  //private def apply( id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port], building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )

  def fromXML( node:Node, hexList:List[Hex] ):Vertex = Vertex(
    id = ( node \ "@id" ).content.toInt,
    h1 = hexList.find( _.id == ( node \ "@h1" ).content.toInt ).get,
    h2 = hexList.find( _.id == ( node \ "@h2" ).content.toInt ).get,
    h3 = hexList.find( _.id == ( node \ "@h3" ).content.toInt ).get,
    port = node.childOf( "port" ).asOption( n => Port.fromXML( n ) ),
    building = node.childOf( "building" ).asOption( n => Structure.fromXML( n ).asInstanceOf[Building] )
  )

  implicit val vertexWrites:Writes[Vertex] = ( o:Vertex ) => Json.obj(
    "id" -> Json.toJson( o.id ),
    "h1" -> Json.toJson( o.h1.id ),
    "h2" -> Json.toJson( o.h2.id ),
    "h3" -> Json.toJson( o.h3.id ),
    "port" -> Json.toJson( o.port ),
    "road" -> Json.toJson( o.building )
  )

  def fromJson( json:JsValue, hexList:List[Hex] ):Vertex = Vertex(
    id = ( json \ "id" ).as[Int],
    h1 = hexList.find( _.id == ( json \ "h1" ).as[Int] ).get,
    h2 = hexList.find( _.id == ( json \ "h2" ).as[Int] ).get,
    h3 = hexList.find( _.id == ( json \ "h3" ).as[Int] ).get,
    port = ( json \ "port" ).asOption[Port],
    building = ( json \ "road" ).asOption[Building]
  )
}

case class Vertex /*private[GameField]*/( id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port] = None, building:Option[Building] = None )
  extends PlacementPoint with XMLSerializable {

  def toXML:Node = <Vertex id={ id.toString } h1={ h1.id.toString } h2={ h2.id.toString } h3={ h3.id.toString }>
    <port>{ port.toXML( _.toXML ) }</port>
    <building>{ building.toXML( _.toXML ) }</building>
  </Vertex>

  //private def copy( ):Unit = {}

  def setBuilding( building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )
}
