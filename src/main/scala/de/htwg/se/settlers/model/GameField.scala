package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.GameField._
import de.htwg.se.settlers.util._

import scala.util.Random

/**
 * @author Vincent76;
 */
case class GameField(
                      hexagons:Hexagons,
                      edges:Edges,
                      vertices:Vertices,
                      robber:Hex
                    ) {

  def findHex( hID:Int ):Option[Hex] = {
    hexagons.foreach( _.foreach( h => if ( h.isDefined && h.get.id == hID ) return h ) )
    Option.empty
  }

  def findHex( r:Int, c:Int ):Option[Hex] = GameField.findHex( r, c, hexagons )

  def adjacentHexes( h:Hex ):List[Hex] = GameField.adjacentHexes( h, hexagons )

  def adjacentHex( h:Hex, ai:Int ):Option[Hex] = {
    if ( ai < adjacentOffset.size ) {
      val o = adjacentOffset( ai )
      return findHex( h.r + o._1, h.c + o._2 )
    }
    Option.empty
  }

  def adjacentEdges( h:Hex ):List[Edge] = {
    adjacentOffset.redByKey( List.empty, ( edges:List[Edge], i:Int ) => {
      val hex1 = findHex( h.r + adjacentOffset( i )._1, h.c + adjacentOffset( i )._2 )
      val edge = if ( hex1.isDefined ) findEdge( h, hex1.get ) else Option.empty
      if ( edge.isDefined )
        edges :+ edge.get
      else
        edges
    } )
  }

  def adjacentVertices( h:Hex ):List[Vertex] = {
    adjacentOffset.redByKey( List.empty, ( vertices:List[Vertex], i:Int ) => {
      val o1 = adjacentOffset( i )
      val o2 = nextAdjacentOffset( i )
      val vertex = getVertex( h, (h.r + o1._1, h.c + o1._2), (h.r + o2._1, h.c + o2._2) )
      if ( vertex.isDefined )
        vertices :+ vertex.get
      else
        vertices
    } )
  }

  private def getVertex( h:Hex, c1:(Int, Int), c2:(Int, Int) ):Option[Vertex] = {
    val hex1 = findHex( c1._1, c1._2 )
    val hex2 = findHex( c2._1, c2._2 )
    if ( hex1.isDefined && hex2.isDefined ) {
      val vertex = findVertex( h, hex1.get, hex2.get )
      if ( vertex.isDefined )
        return Some( vertex.get )
    }
    Option.empty
  }

  def adjacentPlayers( h:Hex ):List[PlayerID] = {
    adjacentVertices( h ).red( List.empty, ( l:List[PlayerID], v:Vertex ) => {
      if ( v.building.isDefined && !l.contains( v.building.get.owner ) )
        l :+ v.building.get.owner
      else l
    } )
  }


  def findEdge( eID:Int ):Option[Edge] = edges.values.find( _.id == eID )

  def findEdge( h:(Hex, Hex) ):Option[Edge] = GameField.findEdge( h._1, h._2, edges )

  def adjacentHexes( e:Edge ):List[Hex] = List( e.h1, e.h2 )

  private def enclosingHexes( e:Edge ):List[Hex] = adjacentHexes( e.h1 ).intersect( adjacentHexes( e.h2 ) )

  def adjacentEdges( e:Edge ):List[Edge] = {
    val enclosedHexes = enclosingHexes( e )
    if ( enclosedHexes.isEmpty )
      return List.empty
    val res1 = findEdge( e.h1, enclosedHexes.head ).toList ++ findEdge( e.h2, enclosedHexes.head ).toList
    if ( enclosedHexes.size > 1 )
      return findEdge( e.h1, enclosedHexes( 1 ) ).toList ++ findEdge( e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1
  }

  def adjacentVertices( e:Edge ):List[Vertex] = {
    val enclosedHexes = enclosingHexes( e )
    if ( enclosedHexes.isEmpty )
      return List.empty
    val res1 = findVertex( e.h1, e.h2, enclosedHexes.head ).toList
    if ( enclosedHexes.size > 1 )
      return findVertex( e.h1, e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1
  }

  def update( e:Edge ):GameField = copy( edges = edges.updated( (e.h1, e.h2), e ) )


  def findVertex( vID:Int ):Option[Vertex] = vertices.values.find( _.id == vID )

  def sortVertexKey( h1:Hex, h2:Hex, h3:Hex ):(Hex, Hex, Hex) = {
    val sorted = List( h1, h2, h3 ).sortBy( _.id )
    (sorted.head, sorted( 1 ), sorted( 2 ))
  }

  def findVertex( h:(Hex, Hex, Hex) ):Option[Vertex] = findVertex( h._1, h._2, h._3 )

  def findVertex( h1:Hex, h2:Hex, h3:Hex ):Option[Vertex] = {
    vertices.get( sortVertexKey( h1, h2, h3 ) ) match {
      case None => Option.empty
      case v => v
    }
  }

  def adjacentEdges( v:Vertex ):List[Edge] = findEdge( v.h1, v.h2 ).toList ++ findEdge( v.h1, v.h3 ).toList ++ findEdge( v.h2, v.h3 )

  def update( v:Vertex ):GameField = copy( vertices = vertices.updated( (v.h1, v.h2, v.h3), v ) )
}


sealed abstract class EdgeDir( val symbol:String )

case object SouthWest extends EdgeDir( "\\" )

case object SouthEast extends EdgeDir( "/" )

case object East extends EdgeDir( "|" )

case object NorthEast extends EdgeDir( "\\" )

case object NorthWest extends EdgeDir( "/" )

case object West extends EdgeDir( "|" )

object GameField {

  def apply( ):GameField = {
    val hexagons = createHexagons
    val edges = createEdges( hexagons )
    val robber = hexagons.deepFind( ( e:Option[Hex] ) => e.isDefined && e.get.area == DesertArea ).get.get
    GameField( hexagons, edges, createVertices( hexagons, edges ), robber )
  }

  case class Hex private[GameField]( id:Int, r:Int, c:Int, area:Area ) {
    private def copy( ):Unit = {}
  }

  object Hex {
    private def apply( id:Int, r:Int, c:Int, area:Area ):Hex = new Hex( id, r, c, area )
  }

  sealed abstract class PlacementPoint {
    def getStructure:Option[Structure]
  }

  case class Edge private[GameField]( id:Int, h1:Hex, h2:Hex, port:Option[Port] = Option.empty, road:Option[Road] = Option.empty )
    extends PlacementPoint {
    def hexes:List[Hex] = List( h1, h2 )

    private def copy( ):Unit = {}

    override def getStructure:Option[Structure] = road

    def setRoad( road:Option[Road] ):Edge = new Edge( id, h1, h2, port, road )
  }

  object Edge {
    private def apply( id:Int, h1:Hex, h2:Hex, port:Option[Port], building:Option[Road] ):Edge = new Edge( id, h1, h2, port, building )
  }

  case class Vertex private[GameField]( id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port] = Option.empty, building:Option[Building] = Option.empty )
    extends PlacementPoint {
    def hexes:List[Hex] = List( h1, h2, h3 )

    private def copy( ):Unit = {}

    override def getStructure:Option[Structure] = building

    def setBuilding( building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )
  }

  object Vertex {
    private def apply( id:Int, h1:Hex, h2:Hex, h3:Hex, port:Option[Port], building:Option[Building] ):Vertex = new Vertex( id, h1, h2, h3, port, building )
  }

  type Row[E] = Vector[Option[E]]

  type Field[E] = Vector[Row[E]]

  type Hexagons = Field[Hex]

  type Edges = Map[(Hex, Hex), Edge]

  type Vertices = Map[(Hex, Hex, Hex), Vertex]

  val adjacentOffset = Vector( (1, -1), (1, 0), (0, 1), (-1, 1), (-1, 0), (0, -1) )


  def findHex[E]( r:Int, c:Int, data:Field[E] ):Option[E] = {
    if ( r >= 0 && r < data.size && c >= 0 && c < data( r ).size )
      return data( r )( c )
    Option.empty
  }

  def adjacentHexes( h:Hex, hexagons:Hexagons ):List[Hex] = {
    adjacentOffset.map( c => findHex( h.r + c._1, h.c + c._2, hexagons ) ).filter( _.isDefined ).map( _.get ).toList
  }

  def findEdge( h1:Hex, h2:Hex, data:Edges ):Option[Edge] = data.get( if ( h1.id < h2.id ) (h1, h2) else (h2, h1) ) match {
    case None => Option.empty
    case e => e
  }

  def nextAdjacentOffset( i:Int ):(Int, Int) = {
    if ( ( i + 1 ) < adjacentOffset.size )
      adjacentOffset( i + 1 )
    else
      adjacentOffset( ( i + 1 ) % adjacentOffset.size )
  }


  def createHexagons:Hexagons = {
    val (hexData:Vector[Vector[Option[(Int, Int, Int)]]], _) = createRow( Vector.empty, 6, 1 )
    type Result = (Hexagons, (List[WaterArea], List[WaterArea], List[Option[Resource]], List[Number]))
    val areas = Area.getAvailableAreas
    hexData.redByKey( (Vector.empty, areas), ( result:Result, i:Int ) => {
      hexData( i ).redByKey( result, ( res:Result, j:Int ) => {
        val data = hexData( i )( j )
        val res1 = if ( i >= res._1.size ) res._1 :+ Vector.empty else res._1
        if ( data.isDefined ) {
          val (adjacent, maxFrequency, port) = adjacentOffset.redByKey( (0, Numbers.maxFrequency, true), ( adjacency:(Int, Int, Boolean), ai:Int ) => {
            val o1 = adjacentOffset( ai )
            val o2 = nextAdjacentOffset( ai )
            val data1 = findHex( i + o1._1, j + o1._2, hexData )
            val hex1 = if ( data1.isDefined ) findHex( data1.get._2, data1.get._3, res1 ) else Option.empty
            val hex2 = findHex( i + o2._1, j + o2._2, hexData ).use( d => if ( d.isDefined ) findHex( d.get._2, d.get._3, res1 ) else Option.empty )
            (
              adjacency._1 + ( if ( data1.isDefined ) 1 else 0 ),
              Math.min( adjacency._2, getMaxFrequency( getFrequencies( hex1 ), getFrequencies( hex2 ) ) ),
              adjacency._3 && ( hex1.isEmpty || ( hex1.get.area match {
                case a:WaterArea => a.port.isEmpty
                case _ => true
              } ) )
            )
          } )
          if ( adjacent < 6 ) {
            val (hex, portAreas, waterAreas) = if ( port && ( res._2._1 != areas._1 || res._2._2 != areas._2 || Random.nextBoolean() ) ) {
              (new Hex( data.get._1, data.get._2, data.get._3, res._2._1.head ), res._2._1.tail, res._2._2)
            } else {
              (new Hex( data.get._1, data.get._2, data.get._3, res._2._2.head ), res._2._1, res._2._2.tail)
            }
            //val hex = Hex( data.get._1, data.get._2, data.get._3, res._2._1.head )
            (res1.updated( i, res1( i ) :+ Some( hex ) ), (portAreas, waterAreas, res._2._3, res._2._4))
          } else {
            val numberIndex = res._2._4.indexWhere( _.frequency <= maxFrequency ).use( i => {
              if ( i >= 0 ) i else if ( res._2._4.nonEmpty ) res._2._4.view.zipWithIndex.minBy( _._1.frequency )._2 else -1
            } )
            val (area, numbers) = res._2._3.head match {
              case Some( x ) => (ResourceArea( x, res._2._4( numberIndex ) ), res._2._4.removeAt( numberIndex ))
              case _ => (DesertArea, res._2._4)
            }
            val hex = Some( new Hex( data.get._1, data.get._2, data.get._3, area ) )
            (res1.updated( i, res1( i ) :+ hex ), (res._2._1, res._2._2, res._2._3.tail, numbers))
          }
        } else
          (res1.updated( i, res1( i ) :+ Option.empty ), res._2)
      } )
    } )._1
  }

  private def getFrequencies( hex:Option[Hex] ):Int = {
    if ( hex.isDefined )
      hex.get.area match {
        case area:ResourceArea => area.number.frequency
        case _ => 0
      }
    else
      0
  }

  private def getMaxFrequency( freq1:Int, freq2:Int ):Int = {
    val max = Numbers.maxSum - freq1 - freq2
    if ( max >= Numbers.maxFrequency && ( freq1 >= Numbers.maxFrequency || freq2 >= Numbers.maxFrequency ) )
      return Numbers.maxFrequency - 1
    max
  }

  private def createRow( field:Field[(Int, Int, Int)], i:Int, count:Int ):(Field[(Int, Int, Int)], Int) = {
    if ( i >= 0 ) {
      val (nRows, nCount) = createRow( field, i - 1, count )
      val (cols, nCount2) = createCols( Vector.empty, i, 6, nCount )
      return (nRows :+ cols, nCount2)
    }
    (field, count)
  }

  private def createCols( row:Row[(Int, Int, Int)], i:Int, j:Int, count:Int ):(Row[(Int, Int, Int)], Int) = {
    if ( j >= 0 ) {
      val (nCols, nCount) = createCols( row, i, j - 1, count )
      val (hex, nCount2) = createCol( i, j, nCount )
      return (nCols :+ hex, nCount2)
    }
    (row, count)
  }

  private def createCol( i:Int, j:Int, count:Int ):(Option[(Int, Int, Int)], Int) = {
    if ( ( ( i + 2 ) * ( j + 2 ) ).check( v => v >= 10 && v <= 42 ) )
      (Some( (count, i, j) ), count + 1)
    else
      (Option.empty, count)
  }


  def createEdges( hexagons:Hexagons ):Edges = {
    hexagons.red( Map.empty:Edges, ( map:Edges, r:Row[Hex] ) => r.red( map, ( m:Edges, hex:Option[Hex] ) => {
      if ( hex.isDefined ) {
        val h = hex.get
        val m1 = addEdge( m, h, (h.r + 1, h.c - 1), hexagons )
        val m2 = addEdge( m1, h, (h.r + 1, h.c), hexagons )
        addEdge( m2, h, (h.r, h.c + 1), hexagons )
      } else m
    } ) )
  }

  def addEdge( m:Edges, h:Hex, c:(Int, Int), hexagons:Hexagons ):Edges = {
    val nHex = findHex( c._1, c._2, hexagons )
    if ( nHex.isDefined )
      return m + ( (h, nHex.get) -> ( if ( isPortHex( h ) && nHex.get.area.isInstanceOf[LandArea] )
        createPortEdge( m, h, h, nHex.get, hexagons )
      else if ( isPortHex( nHex.get ) && h.area.isInstanceOf[LandArea] )
        createPortEdge( m, nHex.get, h, nHex.get, hexagons )
      else
        new Edge( m.size, h, nHex.get )
        ) )
    m
  }

  private def isPortHex( h:Hex ):Boolean = h.area match {
    case a:WaterArea => a.port.isDefined
    case _ => false
  }

  private def createPortEdge( m:Edges, portHex:Hex, h1:Hex, h2:Hex, hexagons:Hexagons ):Edge = {
    val adjacentLandHex = adjacentHexes( portHex, hexagons ).filter( h => {
      h.area.isInstanceOf[LandArea]
    } )
    val landHex = if ( h1 == portHex ) h2 else h1
    val port = portHex.area.asInstanceOf[WaterArea].port
    if ( adjacentLandHex.size > 1 ) {
      val l = adjacentLandHex.filter( h => {
        val edge = m.get( if ( h.id < portHex.id ) (h, portHex) else (portHex, h) )
        if ( edge.isDefined )
          if ( edge.get.port.isDefined )
            return new Edge( m.size, h1, h2 )
          else
            false
        else true
      } )
      Random.element( l ).use( h => {
        if ( h.get == landHex )
          new Edge( m.size, h1, h2, port )
        else
          new Edge( m.size, h1, h2 )
      } )
    } else
      new Edge( m.size, h1, h2, port )
  }


  def createVertices( hexagons:Hexagons, edges:Edges ):Vertices = {
    hexagons.red( Map[(Hex, Hex, Hex), Vertex](), ( map:Vertices, r:Row[Hex] ) => r.red( map, ( m:Vertices, hex:Option[Hex] ) => {
      if ( hex.isDefined ) {
        val h = hex.get
        val m1 = addVertex( m, h, (h.r + 1, h.c - 1), (h.r + 1, h.c), hexagons, edges )
        addVertex( m1, h, (h.r, h.c + 1), (h.r + 1, h.c), hexagons, edges )
      } else
        m
    } ) )
  }

  def addVertex( m:Vertices, h:Hex, c1:(Int, Int), c2:(Int, Int), hexagons:Hexagons, edges:Edges ):Vertices = {
    val hex1 = findHex( c1._1, c1._2, hexagons )
    val hex2 = findHex( c2._1, c2._2, hexagons )
    if ( hex1.isDefined && hex2.isDefined )
      return m + ( (h, hex1.get, hex2.get) -> new Vertex( m.size, h, hex1.get, hex2.get, getPortEdge( h, hex1.get, hex2.get, edges ) ) )
    m
  }

  private def getPortEdge( h1:Hex, h2:Hex, h3:Hex, edges:Edges ):Option[Port] = {
    Vector( findEdge( h1, h2, edges ), findEdge( h1, h3, edges ), findEdge( h2, h3, edges ) ).foreach( e => {
      if ( e.isDefined && e.get.port.isDefined )
        return e.get.port
    } )
    Option.empty
  }
}