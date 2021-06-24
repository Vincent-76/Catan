package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import de.htwg.se.catan.model.impl.fileio.XMLSerializable

import scala.xml.Node


trait GameField extends XMLSerializable {

  def fieldWidth:Int
  def fieldHeight:Int
  def robberHex:Hex
  def setRobberHex( hex:Hex ):GameField

  // Hex

  def hexList:List[Hex]
  def findHex( hID:Int ):Option[Hex]
  def findHex( r:Int, c:Int ):Option[Hex]
  def adjacentHexes( h:Hex ):List[Hex]
  def adjacentHex( h:Hex, ai:Int ):Option[Hex]
  def adjacentEdges( h:Hex ):List[Edge]
  def adjacentEdge( h:Hex, ai:Int ):Option[Edge]
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

object Hex {
  //private def apply( id:Int, r:Int, c:Int, area:Area ):Hex = new Hex( id, r, c, area )

  def fromXML( node:Node ):Hex = Hex(
    id = ( node \ "@id" ).content.toInt,
    r = ( node \ "@r" ).content.toInt,
    c = ( node \ "@c" ).content.toInt,
    area = Area.fromXML( node.childOf( "area" ) )
  )
}

case class Hex /*private[GameField]*/( id:Int, r:Int, c:Int, area:Area ) extends PlacementPoint with XMLSerializable {
  //private def copy( ):Unit = {}

  def toXML:Node = <Hex id={ id.toString } r={ r.toString } c={ c.toString }>
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
    port = node.childOf( "port" ).toOption( n => Port.fromXML( n ) ),
    road = node.childOf( "road" ).toOption( n => Structure.fromXML( n ).asInstanceOf[Road] )
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
    port = node.childOf( "port" ).toOption( n => Port.fromXML( n ) ),
    building = node.childOf( "building" ).toOption( n => Structure.fromXML( n ).asInstanceOf[Building] )
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
