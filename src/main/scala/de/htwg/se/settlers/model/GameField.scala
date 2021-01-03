package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Coords.{ Edges, Vertices }
import de.htwg.se.settlers.util._

import scala.collection.immutable.HashMap

/**
 * @author Vincent76;
 */

case class GameField(
                      areas:HashMap[Hex, Area],
                      edges:Edges,
                      vertices:Vertices
                    ) {

}

case class Hex( r:Int, c:Int, id:Int ) {
  def displayID:String = {
    if( id < 10 )
      "0" + id
    else
      id.toString
  }
}

case class Edge( hex1:Hex, hex2:Hex )

case class Vertex( hex1:Hex, hex2:Hex, hex3:Hex )

object Coords {
  /*val all2:Vector[Vector[Option[Hex]]] = ( 0 until 7 ).red( Vector(), ( res:Vector[Vector[Option[Hex]]], i:Int ) => {
    res :+ ( 0 until 7 ).red( Vector(), ( sub:Vector[Option[Hex]], j:Int ) => {
      sub :+ ( if ( ( ( i + 2 ) * ( j + 2 ) ).check( v => v >= 10 && v <= 42 ) )
        Some( Hex( i, j, 0 ) )
      else
        Option.empty )
    } )
  } )*/

  type Hexagons = Vector[Vector[Option[Hex]]]

  type Edges = Map[(Hex, Hex), Edge]

  type Vertices = Map[(Hex, Hex, Hex), Vertex]

  val all:Hexagons = createHex()

  private def createHex():Hexagons = {
    val (hex, _) = createRow( Vector(), 6, 1 )
    hex
  }

  private def createRow( rows:Hexagons, i:Int, count:Int ):(Hexagons, Int) = {
    if ( i >= 0 ) {
      val (nRows, nCount) = createRow( rows, i - 1, count )
      val (cols, nCount2) = createCols( Vector(), i, 6, nCount )
      return (nRows :+ cols, nCount2)
    }
    (rows, count)
  }

  private def createCols( cols:Vector[Option[Hex]], i:Int, j:Int, count:Int ):(Vector[Option[Hex]], Int) = {
    if ( j >= 0 ) {
      val (nCols, nCount) = createCols( cols, i, j - 1, count )
      val (hex, nCount2) = createCol( i, j, nCount )
      return (nCols :+ hex, nCount2)
    }
    (cols, count)
  }

  private def createCol( i:Int, j:Int, count:Int ):(Option[Hex], Int) = {
    if ( ( ( i + 2 ) * ( j + 2 ) ).check( v => v >= 10 && v <= 42 ) )
      (Some( Hex( i, j, count ) ), count + 1)
    else
      (Option.empty, count)
  }


  def find( r:Int, c:Int ):Option[Hex] = {
    if( r < all.size && c < all( r ).size )
      return all( r )( c )
    Option.empty
  }


  def createEdges( emptyMap:Edges ):Edges = {
    all.red( emptyMap, ( map:Edges, r:Vector[Option[Hex]] ) => r.red( map, ( m:Edges, hex:Option[Hex] ) => {
      if( hex.isDefined ) {
        val h = hex.get
        val m1 = addEdge( m, h, h.r + 1, h.c - 1 )
        val m2 = addEdge( m1, h, h.r + 1, h.c )
        return addEdge( m2, h, h.r, h.c + 1 )
      }
      m
    } ) )
  }

  def addEdge( m:Map[(Hex, Hex), Edge], h:Hex, r:Int, c:Int ):Map[(Hex, Hex), Edge] = {
    val nHex = find( r, c )
    if( nHex.isDefined ) {
      return m + ( (h, nHex.get) -> Edge( h, nHex.get ) )
    }
    m
  }
}