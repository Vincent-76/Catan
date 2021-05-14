package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID

trait GameField {

  // Hex

  def findHex( hID:Int ):Option[Hex]

  def findHex( r:Int, c:Int ):Option[Hex]

  def adjacentHexes( h:Hex ):List[Hex]

  def adjacentHex( h:Hex, ai:Int ):Option[Hex]

  def adjacentEdges( h:Hex ):List[Edge]

  def adjacentVertices( h:Hex ):List[Vertex]

  def adjacentPlayers( h:Hex ):List[PlayerID]


  // Edge

  def findEdge( eID:Int ):Option[Edge]

  def findEdge( h:(Hex, Hex) ):Option[Edge]

  def adjacentHexes( e:Edge ):List[Hex]

  def adjacentEdges( e:Edge ):List[Edge]

  def adjacentVertices( e:Edge ):List[Vertex]

  def update( e:Edge ):GameField


  // Vertex

  def findVertex( vID:Int ):Option[Vertex]

  def findVertex( h:(Hex, Hex, Hex) ):Option[Vertex]

  def adjacentEdges( v:Vertex ):List[Edge]

  def update( v:Vertex ):GameField

}

sealed trait PlacementPoint

case class Hex /*private[GameField]*/(id:Int, r:Int, c:Int, area:Area ) extends PlacementPoint {
  //private def copy( ):Unit = {}

  def isWater:Boolean = area.f == Water

  def isLand:Boolean = area.isInstanceOf[LandArea]
}

/*object Hex {
  private def apply( id:Int, r:Int, c:Int, area:Area ):Hex = new Hex( id, r, c, area )
}*/

case class Edge /*private[GameField]*/(id:Int, h1:Hex, h2:Hex, port:Option[Port] = Option.empty, road:Option[Road] = Option.empty )
  extends PlacementPoint {

  //private def copy( ):Unit = {}

  def setRoad( road:Option[Road] ):Edge = new Edge( id, h1, h2, port, road )
}

/*object Edge {
  private def apply( id:Int, h1:Hex, h2:Hex, port:Option[Port], building:Option[Road] ):Edge = new Edge( id, h1, h2, port, building )
}*/

case class Vertex /*private[GameField]*/(id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port] = Option.empty, building:Option[Building] = Option.empty )
  extends PlacementPoint {

  //private def copy( ):Unit = {}

  def setBuilding( building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )
}

/*object Vertex {
  private def apply( id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port], building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )
}*/
