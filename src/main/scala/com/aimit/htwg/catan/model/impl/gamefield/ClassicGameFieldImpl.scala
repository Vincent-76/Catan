package com.aimit.htwg.catan.model.impl.gamefield

import com.aimit.htwg.catan.model.GameField.{ Edges, Field, Row, Vertices, adjacentOffset }
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.{ JsonLookupResult, JsonMap, JsonSeq, JsonTuple2, JsonTuple3, JsonValue }
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLOption, XMLSequence, XMLTuple2, XMLTuple3 }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.Random
import scala.xml.Node

/**
 * @author Vincent76;
 */
case class ClassicGameFieldImpl( _field:Field[Hex],
                                 edges:Edges,
                                 vertices:Vertices,
                                 robber:Hex
                               ) extends GameField {

  def toXML:Node = <ClassicGameFieldImpl robber={ robber.id.toString }>
    <field>{ field.toXML( _.toXML( _.toXML( _.toXML ) ) ) }</field>
    <edges>{ edges.toXML( _.toXML( _.id.toString, _.id.toString ), _.toXML ) }</edges>
    <vertices>{ vertices.toXML( _.toXML( _.id.toString, _.id.toString, _.id.toString ), _.toXML ) }</vertices>
  </ClassicGameFieldImpl>.copy( label = ClassicGameFieldImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ClassicGameFieldImpl.name ),
    "field" -> field.toJsonC( _.toJsonC( o => Json.toJson( o ) ) ),
    "edges" -> edges.toJsonC( _.toJsonC( h1 => Json.toJson( h1.id ), h2 => Json.toJson( h2.id ) ), e => Json.toJson( e ) ),
    "vertices" -> vertices.toJsonC( _.toJsonC( h1 => Json.toJson( h1.id ), h2 => Json.toJson( h2.id ), h3 => Json.toJson( h3.id ) ), v => Json.toJson( v ) ),
    "robber" -> Json.toJson( robber.id )
  )

  def field:Field[Hex] = _field

  def fieldWidth:Int = field.map( r => r.size ).max
  def fieldHeight:Int = field.size
  def robberHex:Hex = robber
  def setRobberHex( hex:Hex ):GameField = copy( robber = hex )


  def hexList:List[Hex] = field.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList

  def findHex( hID:Int ):Option[Hex] = {
    field.foreach( _.foreach( h => if( h.isDefined && h.get.id == hID ) return h ) )
    Option.empty
  }

  def findHex( row:Int, col:Int ):Option[Hex] = ClassicGameFieldImpl.findHex( row, col, field )

  def adjacentHexes( h:Hex ):List[Hex] = ClassicGameFieldImpl.adjacentHexes( h, field )

  def adjacentHex( h:Hex, offsetIndex:Int ):Option[Hex] = {
    if( offsetIndex < adjacentOffset.size ) {
      val o = adjacentOffset( offsetIndex )
      findHex( h.row + o._1, h.col + o._2 )
    } else None
  }

  def adjacentEdges( h:Hex ):List[Edge] = {
    adjacentOffset.redByKey( List.empty, ( edges:List[Edge], i:Int ) => {
      val hex1 = findHex( h.row + adjacentOffset( i )._1, h.col + adjacentOffset( i )._2 )
      val edge = if( hex1.isDefined ) findEdge( h, hex1.get ) else Option.empty
      if( edge.isDefined )
        edges :+ edge.get
      else
        edges
    } )
  }

  def adjacentEdge( h:Hex, offsetIndex:Int ):Option[Edge] = {
    if( offsetIndex < adjacentOffset.size ) {
      val o = adjacentOffset( offsetIndex )
      val hex1 = findHex( h.row + o._1, h.col + o._2 )
      if( hex1.isDefined ) findEdge( h, hex1.get ) else None
    } else None
  }

  def adjacentVertices( h:Hex ):List[Vertex] = {
    adjacentOffset.redByKey( List.empty, ( vertices:List[Vertex], i:Int ) => {
      val vertex = getVertex( h, adjacentOffset( i ), ClassicGameFieldImpl.nextAdjacentOffset( i ) )
      if( vertex.isDefined )
        vertices :+ vertex.get
      else
        vertices
    } )
  }

  def adjacentVertex( h:Hex, offsetIndex1:Int, offsetIndex2:Int ):Option[Vertex] = {
    if( offsetIndex1 < adjacentOffset.length && offsetIndex2 < adjacentOffset.length )
      getVertex( h, adjacentOffset( offsetIndex1 ), adjacentOffset( offsetIndex2 ) )
    else None
  }

  private def getVertex( h:Hex, c1:(Int, Int), c2:(Int, Int) ):Option[Vertex] = {
    val hex1 = findHex( h.row + c1._1, h.col + c1._2 )
    val hex2 = findHex( h.row + c2._1, h.col + c2._2 )
    if( hex1.isDefined && hex2.isDefined )
      findVertex( h, hex1.get, hex2.get )
    else None
  }

  def adjacentPlayers( h:Hex ):List[PlayerID] = {
    adjacentVertices( h ).red( List.empty, ( l:List[PlayerID], v:Vertex ) => {
      if( v.building.isDefined && !l.contains( v.building.get.owner ) )
        l :+ v.building.get.owner
      else l
    } )
  }


  def edgeList:List[Edge] = edges.values.toList

  def findEdge( eID:Int ):Option[Edge] = edges.values.find( _.id == eID )

  def findEdge( h:(Hex, Hex) ):Option[Edge] = ClassicGameFieldImpl.findEdge( h._1, h._2, edges )

  def adjacentHexes( e:Edge ):List[Hex] = List( e.h1, e.h2 )

  private def enclosingHexes( e:Edge ):List[Hex] = adjacentHexes( e.h1 ).intersect( adjacentHexes( e.h2 ) )

  def adjacentEdges( e:Edge ):List[Edge] = {
    val enclosedHexes = enclosingHexes( e )
    /*if ( enclosedHexes.isEmpty )
      return List.empty*/
    val res1 = findEdge( e.h1, enclosedHexes.head ).toList ++ findEdge( e.h2, enclosedHexes.head ).toList
    if( enclosedHexes.size > 1 )
      return findEdge( e.h1, enclosedHexes( 1 ) ).toList ++ findEdge( e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1
  }

  def adjacentVertices( e:Edge ):List[Vertex] = {
    val enclosedHexes = enclosingHexes( e )
    /*if ( enclosedHexes.isEmpty )
      return List.empty*/
    val res1 = findVertex( e.h1, e.h2, enclosedHexes.head ).toList
    if( enclosedHexes.size > 1 )
      return findVertex( e.h1, e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1
  }

  def update( e:Edge ):GameField = copy( edges = edges.updated( (e.h1, e.h2), e ) )


  def vertexList:List[Vertex] = vertices.values.toList

  def findVertex( vID:Int ):Option[Vertex] = vertices.values.find( _.id == vID )

  private def sortVertexKey( h1:Hex, h2:Hex, h3:Hex ):(Hex, Hex, Hex) = {
    val sorted = List( h1, h2, h3 ).sortBy( _.id )
    (sorted.head, sorted( 1 ), sorted( 2 ))
  }

  def findVertex( h:(Hex, Hex, Hex) ):Option[Vertex] = vertices.get( sortVertexKey( h._1, h._2, h._3 ) )

  def adjacentEdges( v:Vertex ):List[Edge] = findEdge( v.h1, v.h2 ).toList ++ findEdge( v.h1, v.h3 ).toList ++ findEdge( v.h2, v.h3 )

  def update( v:Vertex ):GameField = copy( vertices = vertices.updated( (v.h1, v.h2, v.h3), v ) )
}

object ClassicGameFieldImpl extends GameFieldImpl( "ClassicGameFieldImpl" ) {

  def fromXML( node:Node ):ClassicGameFieldImpl = {
    val field = node.childOf( "field" ).asVector( _.asVector( _.asOption( n => Hex.fromXML( n ) ) ) )
    val hexList = field.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList
    ClassicGameFieldImpl(
      field,
      edges = node.childOf( "edges" ).asMapC( ( keyNode, valNode ) => {
        val edge = Edge.fromXML( valNode, hexList )
        ((edge.h1, edge.h2), edge)
      } ),
      vertices = node.childOf( "vertices" ).asMapC( ( keyNode, valNode ) => {
        val vertex = Vertex.fromXML( valNode, hexList )
        ((vertex.h1, vertex.h2, vertex.h3), vertex)
      } ),
      robber = hexList.find( _.id == ( node \ "@robber" ).content.toInt ).get
    )
  }

  def fromJson( json:JsValue ):ClassicGameFieldImpl = {
    val field = ( json \ "field" ).asVectorC( _.asVectorC( _.asOption[Hex] ) )
    val hexList = field.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList
    ClassicGameFieldImpl(
      field,
      edges = ( json \ "edges" ).asMapC( _.asTupleC(
        v1 => hexList.find( _.id == v1.as[Int] ).get,
        v2 => hexList.find( _.id == v2.as[Int] ).get
      ), v => Edge.fromJson( v, hexList ) ),
      vertices = ( json \ "vertices" ).asMapC( _.asTupleC(
        v1 => hexList.find( _.id == v1.as[Int] ).get,
        v2 => hexList.find( _.id == v2.as[Int] ).get,
        v3 => hexList.find( _.id == v3.as[Int] ).get
      ), v => Vertex.fromJson( v, hexList ) ),
      robber = hexList.find( _.id == ( json \ "robber" ).as[Int] ).get
    )
  }


  def apply( seed:Int ):ClassicGameFieldImpl = {
    val random = new Random( seed )
    val field = createField( random )
    val edges = createEdges( field, random )
    val robber = field.deepFind( ( e:Option[Hex] ) => e.isDefined && e.get.area.isInstanceOf[DesertArea] ).get.get
    ClassicGameFieldImpl( field, edges, createVertices( field, edges ), robber )
  }


  def findHex[E]( r:Int, c:Int, data:Field[E] ):Option[E] = {
    if( r >= 0 && r < data.size && c >= 0 && c < data( r ).size )
      return data( r )( c )
    None
  }

  def adjacentHexes( h:Hex, field:Field[Hex] ):List[Hex] = {
    adjacentOffset.map( c => findHex( h.row + c._1, h.col + c._2, field ) ).filter( _.isDefined ).map( _.get ).toList
  }

  def findEdge( h1:Hex, h2:Hex, data:Edges ):Option[Edge] = data.get( if( h1.id < h2.id ) (h1, h2) else (h2, h1) ) match {
    case None => None
    case e => e
  }

  def nextAdjacentOffset( i:Int ):(Int, Int) = {
    if( (i + 1) < adjacentOffset.size )
      adjacentOffset( i + 1 )
    else
      adjacentOffset( (i + 1) % adjacentOffset.size )
  }


  def createField( random:Random = Random ):Field[Hex] = {
    val (hexData:Vector[Vector[Option[(Int, Int, Int)]]], _) = createRow( Vector.empty, 6, 1 )
    type Result = (Field[Hex], (List[WaterArea], List[WaterArea], List[Option[Resource]], List[DiceValue]))
    val areas = getAvailableAreas( random )
    hexData.redByKey( (Vector.empty, areas), ( result:Result, i:Int ) => {
      hexData( i ).redByKey( result, ( res:Result, j:Int ) => {
        val data = hexData( i )( j )
        val res1 = if( i >= res._1.size ) res._1 :+ Vector.empty else res._1
        if( data.isDefined ) {
          val (adjacent, maxFrequency, port) = adjacentOffset.redByKey( (0, DiceValue.maxFrequency, true), ( adjacency:(Int, Int, Boolean), ai:Int ) => {
            val o1 = adjacentOffset( ai )
            val o2 = nextAdjacentOffset( ai )
            val data1 = findHex( i + o1._1, j + o1._2, hexData )
            val hex1 = if( data1.isDefined ) findHex( data1.get._2, data1.get._3, res1 ) else None
            val hex2 = findHex( i + o2._1, j + o2._2, hexData ).use( d => if( d.isDefined ) findHex( d.get._2, d.get._3, res1 ) else None )
            (
              adjacency._1 + (if( data1.isDefined ) 1 else 0),
              Math.min( adjacency._2, getMaxFrequency( getFrequencies( hex1 ), getFrequencies( hex2 ) ) ),
              adjacency._3 && (hex1.isEmpty || (hex1.get.area match {
                case a:WaterArea => a.port.isEmpty
                case _ => true
              }))
            )
          } )
          if( adjacent < 6 ) {
            val (hex, portAreas, waterAreas) = if( port && (res._2._1 != areas._1 || res._2._2 != areas._2 || random.nextBoolean()) ) {
              (new Hex( data.get._1, data.get._2, data.get._3, res._2._1.head ), res._2._1.tail, res._2._2)
            } else {
              (new Hex( data.get._1, data.get._2, data.get._3, res._2._2.head ), res._2._1, res._2._2.tail)
            }
            //val hex = Hex( data.get._1, data.get._2, data.get._3, res._2._1.head )
            (res1.updated( i, res1( i ) :+ Some( hex ) ), (portAreas, waterAreas, res._2._3, res._2._4))
          } else {
            val numberIndex = res._2._4.indexWhere( p => p.frequency <= maxFrequency ).use( i => {
              if( i >= 0 ) i else if( res._2._4.nonEmpty ) res._2._4.view.zipWithIndex.minBy( _._1.frequency )._2 else -1
            } )
            val (area, numbers) = res._2._3.head match {
              case Some( x ) => (ResourceArea( x, res._2._4( numberIndex ) ), res._2._4.removeAt( numberIndex ))
              case _ => (DesertArea(), res._2._4)
            }
            val hex = Some( new Hex( data.get._1, data.get._2, data.get._3, area ) )
            (res1.updated( i, res1( i ) :+ hex ), (res._2._1, res._2._2, res._2._3.tail, numbers))
          }
        } else
          (res1.updated( i, res1( i ) :+ None ), res._2)
      } )
    } )._1
  }

  private def getFrequencies( hex:Option[Hex] ):Int = {
    if( hex.isDefined )
      hex.get.area match {
        case area:ResourceArea => area.number.frequency
        case _ => 0
      }
    else
      0
  }

  private def getMaxFrequency( freq1:Int, freq2:Int ):Int = {
    val max = DiceValue.maxSum - freq1 - freq2
    if( max >= DiceValue.maxFrequency && (freq1 >= DiceValue.maxFrequency || freq2 >= DiceValue.maxFrequency) )
      return DiceValue.maxFrequency - 1
    max
  }

  private def createRow( field:Field[(Int, Int, Int)], i:Int, count:Int ):(Field[(Int, Int, Int)], Int) = {
    if( i >= 0 ) {
      val (nRows, nCount) = createRow( field, i - 1, count )
      val (cols, nCount2) = createCols( Vector.empty, i, 6, nCount )
      return (nRows :+ cols, nCount2)
    }
    (field, count)
  }

  private def createCols( row:Row[(Int, Int, Int)], i:Int, j:Int, count:Int ):(Row[(Int, Int, Int)], Int) = {
    if( j >= 0 ) {
      val (nCols, nCount) = createCols( row, i, j - 1, count )
      val (hex, nCount2) = createCol( i, j, nCount )
      return (nCols :+ hex, nCount2)
    }
    (row, count)
  }

  private def createCol( i:Int, j:Int, count:Int ):(Option[(Int, Int, Int)], Int) = {
    if( ((i + 2) * (j + 2)).check( v => v >= 10 && v <= 42 ) )
      (Some( (count, i, j) ), count + 1)
    else
      (None, count)
  }


  def createEdges( field:Field[Hex], random:Random ):Edges = {
    field.red( Map.empty:Edges, ( map:Edges, r:Row[Hex] ) => r.red( map, ( m:Edges, hex:Option[Hex] ) => {
      if( hex.isDefined ) {
        val h = hex.get
        val m1 = addEdge( m, h, (h.row + 1, h.col - 1), field, random )
        val m2 = addEdge( m1, h, (h.row + 1, h.col), field, random )
        addEdge( m2, h, (h.row, h.col + 1), field, random )
      } else m
    } ) )
  }

  def addEdge( m:Edges, h:Hex, c:(Int, Int), field:Field[Hex], random:Random ):Edges = {
    val nHex = findHex( c._1, c._2, field )
    if( nHex.isDefined )
      return m + ((h, nHex.get) -> (if( isPortHex( h ) && nHex.get.area.isInstanceOf[LandArea] )
        createPortEdge( m, h, h, nHex.get, field, random )
      else if( isPortHex( nHex.get ) && h.area.isInstanceOf[LandArea] )
        createPortEdge( m, nHex.get, h, nHex.get, field, random )
      else
        new Edge( m.size, h, nHex.get )
        ))
    m
  }

  private def isPortHex( h:Hex ):Boolean = h.area match {
    case a:WaterArea => a.port.isDefined
    case _ => false
  }

  private def createPortEdge( m:Edges, portHex:Hex, h1:Hex, h2:Hex, field:Field[Hex], random:Random ):Edge = {
    val adjacentLandHex = adjacentHexes( portHex, field ).filter( h => {
      h.area.isInstanceOf[LandArea]
    } )
    val landHex = if( h1 == portHex ) h2 else h1
    val port = portHex.area.asInstanceOf[WaterArea].port
    if( adjacentLandHex.size > 1 ) {
      val l = adjacentLandHex.filter( h => {
        val edge = m.get( if( h.id < portHex.id ) (h, portHex) else (portHex, h) )
        if( edge.isDefined )
          if( edge.get.port.isDefined )
            return new Edge( m.size, h1, h2 )
          else
            false
        else true
      } )
      random.element( l ).use( h => {
        if( h.get == landHex )
          new Edge( m.size, h1, h2, port )
        else
          new Edge( m.size, h1, h2 )
      } )
    } else
      new Edge( m.size, h1, h2, port )
  }


  def createVertices( field:Field[Hex], edges:Edges ):Vertices = {
    field.red( Map[(Hex, Hex, Hex), Vertex](), ( map:Vertices, r:Row[Hex] ) => r.red( map, ( m:Vertices, hex:Option[Hex] ) => {
      if( hex.isDefined ) {
        val h = hex.get
        val m1 = addVertex( m, h, (h.row + 1, h.col - 1), (h.row + 1, h.col), field, edges )
        addVertex( m1, h, (h.row, h.col + 1), (h.row + 1, h.col), field, edges )
      } else
        m
    } ) )
  }

  def addVertex( m:Vertices, h:Hex, c1:(Int, Int), c2:(Int, Int), field:Field[Hex], edges:Edges ):Vertices = {
    val hex1 = findHex( c1._1, c1._2, field )
    val hex2 = findHex( c2._1, c2._2, field )
    if( hex1.isDefined && hex2.isDefined )
      return m + ((h, hex1.get, hex2.get) -> new Vertex( m.size, h, hex1.get, hex2.get, getPortEdge( h, hex1.get, hex2.get, edges ) ))
    m
  }

  private def getPortEdge( h1:Hex, h2:Hex, h3:Hex, edges:Edges ):Option[Port] = {
    Vector( findEdge( h1, h2, edges ), findEdge( h1, h3, edges ), findEdge( h2, h3, edges ) ).foreach( e => {
      if( e.isDefined && e.get.port.isDefined )
        return e.get.port
    } )
    None
  }


  private def getAvailableAreas( r:Random = Random ):(List[WaterArea], List[WaterArea], List[Option[Resource]], List[DiceValue]) = {
    val portAreas = List(
      WaterArea( Some( Port() ) ),
      WaterArea( Some( Port() ) ),
      WaterArea( Some( Port() ) ),
      WaterArea( Some( Port() ) ),
      WaterArea( Some( Port( Some( Wood ) ) ) ),
      WaterArea( Some( Port( Some( Clay ) ) ) ),
      WaterArea( Some( Port( Some( Sheep ) ) ) ),
      WaterArea( Some( Port( Some( Wheat ) ) ) ),
      WaterArea( Some( Port( Some( Ore ) ) ) )
    )
    val waterAreas = List(
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea(),
      WaterArea()
    )
    val landAreas = List(
      None,
      Some( Wood ),
      Some( Wood ),
      Some( Wood ),
      Some( Wood ),
      Some( Clay ),
      Some( Clay ),
      Some( Clay ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Ore ),
      Some( Ore ),
      Some( Ore ),
    )
    val numbers = List(
      Two,
      Three,
      Three,
      Four,
      Four,
      Five,
      Five,
      Six,
      Six,
      Eight,
      Eight,
      Nine,
      Nine,
      Ten,
      Ten,
      Eleven,
      Eleven,
      Twelve,
    )
    (r.shuffle( portAreas ), waterAreas, r.shuffle( landAreas ), r.shuffle( numbers ))
  }
}