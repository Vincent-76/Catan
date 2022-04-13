package de.htwg.se.catan.model.impl.gamefield

import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.{ JsonLookupResult, JsonMap, JsonSeq, JsonTuple2, JsonTuple3, JsonValue }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLOption, XMLSequence, XMLTuple2, XMLTuple3 }
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl.{ Edges, Hexagons, Vertices }
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.Random
import scala.xml.Node

/**
 * @author Vincent76;
 */
case class ClassicGameFieldImpl( hexagons:Hexagons,
                                 edges:Edges,
                                 vertices:Vertices,
                                 robber:Hex
                               ) extends GameField:

  def toXML:Node = <ClassicGameFieldImpl robber={ robber.id.toString }>
    <hexagons>{ hexagons.toXML( _.toXML( _.toXML( _.toXML ) ) ) }</hexagons>
    <edges>{ edges.toXML( _.toXML( _.id.toString, _.id.toString ), _.toXML ) }</edges>
    <vertices>{ vertices.toXML( _.toXML( _.id.toString, _.id.toString, _.id.toString ), _.toXML ) }</vertices>
  </ClassicGameFieldImpl>.copy( label = ClassicGameFieldImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ClassicGameFieldImpl.name ),
    "hexagons" -> hexagons.toJsonC( _.toJsonC( o => Json.toJson( o ) ) ),
    "edges" -> edges.toJsonC( _.toJsonC( h1 => Json.toJson( h1.id ), h2 => Json.toJson( h2.id ) ), e => Json.toJson( e ) ),
    "vertices" -> vertices.toJsonC( _.toJsonC( h1 => Json.toJson( h1.id ), h2 => Json.toJson( h2.id ), h3 => Json.toJson( h3.id ) ), v => Json.toJson( v ) ),
    "robber" -> Json.toJson( robber.id )
  )


  def fieldWidth:Int = hexagons.map( r => r.size ).max
  def fieldHeight:Int = hexagons.size
  def robberHex:Hex = robber
  def setRobberHex( hex:Hex ):GameField = copy( robber = hex )


  def hexList:List[Hex] = hexagons.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList

  def findHex( hID:Int ):Option[Hex] =
    hexagons.foreach( _.foreach( h => if( h.isDefined && h.get.id == hID ) return h ) )
    Option.empty

  def findHex( r:Int, c:Int ):Option[Hex] = ClassicGameFieldImpl.findHex( r, c, hexagons )

  def adjacentHexes( h:Hex ):List[Hex] = ClassicGameFieldImpl.adjacentHexes( h, hexagons )

  def adjacentHex( h:Hex, ai:Int ):Option[Hex] =
    if ai < ClassicGameFieldImpl.adjacentOffset.size then
      val o = ClassicGameFieldImpl.adjacentOffset( ai )
      findHex( h.r + o._1, h.c + o._2 )
    else None

  def adjacentEdges( h:Hex ):List[Edge] =
    ClassicGameFieldImpl.adjacentOffset.redByKey( List.empty, ( edges:List[Edge], i:Int ) => {
      val hex1 = findHex( h.r + ClassicGameFieldImpl.adjacentOffset( i )._1, h.c + ClassicGameFieldImpl.adjacentOffset( i )._2 )
      val edge = if hex1.isDefined then findEdge( h, hex1.get ) else Option.empty
      if edge.isDefined then
        edges :+ edge.get
      else
        edges
    } )

  def adjacentEdge( h:Hex, ai:Int ):Option[Edge] =
    if ai < ClassicGameFieldImpl.adjacentOffset.size then
      val o = ClassicGameFieldImpl.adjacentOffset( ai )
      val hex1 = findHex( h.r + o._1, h.c + o._2 )
      if hex1.isDefined then findEdge( h, hex1.get ) else None
    else None

  def adjacentVertices( h:Hex ):List[Vertex] =
    ClassicGameFieldImpl.adjacentOffset.redByKey( List.empty, ( vertices:List[Vertex], i:Int ) => {
      val o1 = ClassicGameFieldImpl.adjacentOffset( i )
      val o2 = ClassicGameFieldImpl.nextAdjacentOffset( i )
      val vertex = getVertex( h, (h.r + o1._1, h.c + o1._2), (h.r + o2._1, h.c + o2._2) )
      if vertex.isDefined then
        vertices :+ vertex.get
      else
        vertices
    } )

  private def getVertex( h:Hex, c1:(Int, Int), c2:(Int, Int) ):Option[Vertex] =
    val hex1 = findHex( c1._1, c1._2 )
    val hex2 = findHex( c2._1, c2._2 )
    if hex1.isDefined && hex2.isDefined then
      findVertex( h, hex1.get, hex2.get )
    else None

  def adjacentPlayers( h:Hex ):List[PlayerID] =
    adjacentVertices( h ).red( List.empty, ( l:List[PlayerID], v:Vertex ) => {
      if v.building.isDefined && !l.contains( v.building.get.owner ) then
        l :+ v.building.get.owner
      else l
    } )


  def edgeList:List[Edge] = edges.values.toList

  def findEdge( eID:Int ):Option[Edge] = edges.values.find( _.id == eID )

  def findEdge( h:(Hex, Hex) ):Option[Edge] = ClassicGameFieldImpl.findEdge( h._1, h._2, edges )

  def adjacentHexes( e:Edge ):List[Hex] = List( e.h1, e.h2 )

  private def enclosingHexes( e:Edge ):List[Hex] = adjacentHexes( e.h1 ).intersect( adjacentHexes( e.h2 ) )

  def adjacentEdges( e:Edge ):List[Edge] =
    val enclosedHexes = enclosingHexes( e )
    /*if ( enclosedHexes.isEmpty )
      return List.empty*/
    val res1 = findEdge( e.h1, enclosedHexes.head ).toList ++ findEdge( e.h2, enclosedHexes.head ).toList
    if enclosedHexes.size > 1 then
      return findEdge( e.h1, enclosedHexes( 1 ) ).toList ++ findEdge( e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1

  def adjacentVertices( e:Edge ):List[Vertex] =
    val enclosedHexes = enclosingHexes( e )
    /*if ( enclosedHexes.isEmpty )
      return List.empty*/
    val res1 = findVertex( e.h1, e.h2, enclosedHexes.head ).toList
    if enclosedHexes.size > 1 then
      return findVertex( e.h1, e.h2, enclosedHexes( 1 ) ).toList ++ res1
    res1

  def update( e:Edge ):GameField = copy( edges = edges.updated( (e.h1, e.h2), e ) )


  def vertexList:List[Vertex] = vertices.values.toList

  def findVertex( vID:Int ):Option[Vertex] = vertices.values.find( _.id == vID )

  private def sortVertexKey( h1:Hex, h2:Hex, h3:Hex ):(Hex, Hex, Hex) =
    val sorted = List( h1, h2, h3 ).sortBy( _.id )
    (sorted.head, sorted( 1 ), sorted( 2 ))

  def findVertex( h:(Hex, Hex, Hex) ):Option[Vertex] = vertices.get( sortVertexKey( h._1, h._2, h._3 ) )

  def adjacentEdges( v:Vertex ):List[Edge] = findEdge( v.h1, v.h2 ).toList ++ findEdge( v.h1, v.h3 ).toList ++ findEdge( v.h2, v.h3 )

  def update( v:Vertex ):GameField = copy( vertices = vertices.updated( (v.h1, v.h2, v.h3), v ) )
  

object ClassicGameFieldImpl extends GameFieldImpl( "ClassicGameFieldImpl" ):

  def fromXML( node:Node ):ClassicGameFieldImpl =
    val hexagons = node.childOf( "hexagons" ).asVector( _.asVector( _.asOption( n => Hex.fromXML( n ) ) ) )
    val hexList = hexagons.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList
    ClassicGameFieldImpl(
      hexagons = hexagons,
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

  def fromJson( json:JsValue ):ClassicGameFieldImpl =
    val hexagons = ( json \ "hexagons" ).asVectorC( _.asVectorC( _.asOption[Hex] ) )
    val hexList = hexagons.flatMap( _.filter( _.isDefined ).map( _.get ) ).toList
    ClassicGameFieldImpl(
      hexagons = hexagons,
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


  def apply( seed:Int ):ClassicGameFieldImpl =
    val random = Random( seed )
    val hexagons = createHexagons( random )
    val edges = createEdges( hexagons, random )
    val robber = hexagons.deepFind( ( e:Option[Hex] ) => e.isDefined && e.get.area.isInstanceOf[DesertArea] ).get.get
    ClassicGameFieldImpl( hexagons, edges, createVertices( hexagons, edges ), robber )

  type Row[E] = Vector[Option[E]]

  type Field[E] = Vector[Row[E]]

  type Hexagons = Field[Hex]

  type Edges = Map[(Hex, Hex), Edge]

  type Vertices = Map[(Hex, Hex, Hex), Vertex]

  val adjacentOffset = Vector( (1, -1), (1, 0), (0, 1), (-1, 1), (-1, 0), (0, -1) )


  def findHex[E]( r:Int, c:Int, data:Field[E] ):Option[E] =
    if r >= 0 && r < data.size && c >= 0 && c < data( r ).size then
      return data( r )( c )
    None

  def adjacentHexes( h:Hex, hexagons:Hexagons ):List[Hex] =
    adjacentOffset.map( c => findHex( h.r + c._1, h.c + c._2, hexagons ) ).filter( _.isDefined ).map( _.get ).toList

  def findEdge( h1:Hex, h2:Hex, data:Edges ):Option[Edge] = data.get( if( h1.id < h2.id ) (h1, h2) else (h2, h1) ) match
    case None => None
    case e => e

  def nextAdjacentOffset( i:Int ):(Int, Int) =
    if (i + 1) < adjacentOffset.size then
      adjacentOffset( i + 1 )
    else
      adjacentOffset( (i + 1) % adjacentOffset.size )


  def createHexagons( random:Random = Random ):Hexagons = 
    val (hexData:Vector[Vector[Option[(Int, Int, Int)]]], _) = createRow( Vector.empty, 6, 1 )
    type Result = (Hexagons, (List[WaterArea], List[WaterArea], List[Option[Resource]], List[DiceValue]))
    val areas = getAvailableAreas( random )
    hexData.redByKey( (Vector.empty, areas), ( result:Result, i:Int ) => {
      hexData( i ).redByKey( result, ( res:Result, j:Int ) => {
        val data = hexData( i )( j )
        val res1 = if i >= res._1.size then res._1 :+ Vector.empty else res._1
        if data.isDefined then
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
          if adjacent < 6 then
            val (hex, portAreas, waterAreas) = if port && (res._2._1 != areas._1 || res._2._2 != areas._2 || random.nextBoolean()) then
              (Hex( data.get._1, data.get._2, data.get._3, res._2._1.head ), res._2._1.tail, res._2._2)
            else
              (Hex( data.get._1, data.get._2, data.get._3, res._2._2.head ), res._2._1, res._2._2.tail)
            //val hex = Hex( data.get._1, data.get._2, data.get._3, res._2._1.head )
            (res1.updated( i, res1( i ) :+ Some( hex ) ), (portAreas, waterAreas, res._2._3, res._2._4))
          else
            val numberIndex = res._2._4.indexWhere( p => p.frequency <= maxFrequency ).use( i => {
              if i >= 0 then i else if res._2._4.nonEmpty then res._2._4.view.zipWithIndex.minBy( _._1.frequency )._2 else -1
            } )
            val (area, numbers) = res._2._3.head match
              case Some( x ) => (ResourceArea( x, res._2._4( numberIndex ) ), res._2._4.removeAt( numberIndex ))
              case _ => (DesertArea(), res._2._4)
            val hex = Some( Hex( data.get._1, data.get._2, data.get._3, area ) )
            (res1.updated( i, res1( i ) :+ hex ), (res._2._1, res._2._2, res._2._3.tail, numbers))
        else
          (res1.updated( i, res1( i ) :+ None ), res._2)
      } )
    } )._1

  private def getFrequencies( hex:Option[Hex] ):Int =
    if hex.isDefined then
      hex.get.area match
        case area:ResourceArea => area.number.frequency
        case _ => 0
    else 0

  private def getMaxFrequency( freq1:Int, freq2:Int ):Int =
    val max = DiceValue.maxSum - freq1 - freq2
    if max >= DiceValue.maxFrequency && (freq1 >= DiceValue.maxFrequency || freq2 >= DiceValue.maxFrequency) then
      return DiceValue.maxFrequency - 1
    max

  private def createRow( field:Field[(Int, Int, Int)], i:Int, count:Int ):(Field[(Int, Int, Int)], Int) =
    if i >= 0 then
      val (nRows, nCount) = createRow( field, i - 1, count )
      val (cols, nCount2) = createCols( Vector.empty, i, 6, nCount )
      return (nRows :+ cols, nCount2)
    (field, count)

  private def createCols( row:Row[(Int, Int, Int)], i:Int, j:Int, count:Int ):(Row[(Int, Int, Int)], Int) =
    if j >= 0 then
      val (nCols, nCount) = createCols( row, i, j - 1, count )
      val (hex, nCount2) = createCol( i, j, nCount )
      return (nCols :+ hex, nCount2)
    (row, count)

  private def createCol( i:Int, j:Int, count:Int ):(Option[(Int, Int, Int)], Int) =
    if ((i + 2) * (j + 2)).check( v => v >= 10 && v <= 42 ) then
      (Some( (count, i, j) ), count + 1)
    else
      (None, count)


  def createEdges( hexagons:Hexagons, random:Random ):Edges =
    hexagons.red( Map.empty:Edges, ( map:Edges, r:Row[Hex] ) => r.red( map, ( m:Edges, hex:Option[Hex] ) => {
      if hex.isDefined then
        val h = hex.get
        val m1 = addEdge( m, h, (h.r + 1, h.c - 1), hexagons, random )
        val m2 = addEdge( m1, h, (h.r + 1, h.c), hexagons, random )
        addEdge( m2, h, (h.r, h.c + 1), hexagons, random )
      else m
    } ) )

  def addEdge( m:Edges, h:Hex, c:(Int, Int), hexagons:Hexagons, random:Random ):Edges =
    val nHex = findHex( c._1, c._2, hexagons )
    if nHex.isDefined then
      m + ((h, nHex.get) -> (
        if( isPortHex( h ) && nHex.get.area.isInstanceOf[LandArea] )
          createPortEdge( m, h, h, nHex.get, hexagons, random )
        else if isPortHex( nHex.get ) && h.area.isInstanceOf[LandArea] then
          createPortEdge( m, nHex.get, h, nHex.get, hexagons, random )
        else Edge( m.size, h, nHex.get )
      ))
    else m

  private def isPortHex( h:Hex ):Boolean = h.area match
    case a:WaterArea => a.port.isDefined
    case _ => false

  private def createPortEdge( m:Edges, portHex:Hex, h1:Hex, h2:Hex, hexagons:Hexagons, random:Random ):Edge =
    val adjacentLandHex = adjacentHexes( portHex, hexagons ).filter( h => {
      h.area.isInstanceOf[LandArea]
    } )
    val landHex = if h1 == portHex then h2 else h1
    val port = portHex.area.asInstanceOf[WaterArea].port
    if adjacentLandHex.size > 1 then
      val l = adjacentLandHex.filter( h => {
        val edge = m.get( if h.id < portHex.id then (h, portHex) else (portHex, h) )
        if edge.isDefined then
          if edge.get.port.isDefined then
            return Edge( m.size, h1, h2 )
          else false
        else true
      } )
      random.element( l ).use( h => {
        if h.get == landHex then
          Edge( m.size, h1, h2, port )
        else
          Edge( m.size, h1, h2 )
      } )
    else Edge( m.size, h1, h2, port )


  def createVertices( hexagons:Hexagons, edges:Edges ):Vertices =
    hexagons.red( Map[(Hex, Hex, Hex), Vertex](), ( map:Vertices, r:Row[Hex] ) => r.red( map, ( m:Vertices, hex:Option[Hex] ) => {
      if hex.isDefined then
        val h = hex.get
        val m1 = addVertex( m, h, (h.r + 1, h.c - 1), (h.r + 1, h.c), hexagons, edges )
        addVertex( m1, h, (h.r, h.c + 1), (h.r + 1, h.c), hexagons, edges )
      else m
    } ) )

  def addVertex( m:Vertices, h:Hex, c1:(Int, Int), c2:(Int, Int), hexagons:Hexagons, edges:Edges ):Vertices =
    val hex1 = findHex( c1._1, c1._2, hexagons )
    val hex2 = findHex( c2._1, c2._2, hexagons )
    if hex1.isDefined && hex2.isDefined then
      m + ((h, hex1.get, hex2.get) -> Vertex( m.size, h, hex1.get, hex2.get, getPortEdge( h, hex1.get, hex2.get, edges ) ))
    else m

  private def getPortEdge( h1:Hex, h2:Hex, h3:Hex, edges:Edges ):Option[Port] =
    Vector( findEdge( h1, h2, edges ), findEdge( h1, h3, edges ), findEdge( h2, h3, edges ) ).foreach( e => {
      if e.isDefined && e.get.port.isDefined then
        return e.get.port
    } )
    None


  private def getAvailableAreas( r:Random = Random ):(List[WaterArea], List[WaterArea], List[Option[Resource]], List[DiceValue]) =
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
      DiceValue.Two,
      DiceValue.Three,
      DiceValue.Three,
      DiceValue.Four,
      DiceValue.Four,
      DiceValue.Five,
      DiceValue.Five,
      DiceValue.Six,
      DiceValue.Six,
      DiceValue.Eight,
      DiceValue.Eight,
      DiceValue.Nine,
      DiceValue.Nine,
      DiceValue.Ten,
      DiceValue.Ten,
      DiceValue.Eleven,
      DiceValue.Eleven,
      DiceValue.Twelve,
    )
    (r.shuffle( portAreas ), waterAreas, r.shuffle( landAreas ), r.shuffle( numbers ))