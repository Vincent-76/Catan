package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.GameField.{Edge, Hex, Vertex}
import de.htwg.se.settlers.util._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

/**
 * @author Vincent76;
 */
class GameFieldSpec extends WordSpec with Matchers {
  "GameField" when {
    "new" should {
      val gameField = GameField( new Random( 1 ) )
      "have a size" in {
        gameField.hexagons.red( 0, ( i:Int, r:Vector[Option[Hex]] ) => {
          r.red( i, ( j:Int, h:Option[Hex] ) => j + ( if ( h.isDefined ) 1 else 0 ) )
        } ) should be( 37 )
      }
      val hexOption = gameField.findHex( 4, 5 )
      val hexOption1 = gameField.findHex( 33 )
      val hexOption2 = gameField.findHex( 22 )
      val hexOption3 = gameField.findHex( 21 )
      val hexOption4 = gameField.findHex( 27 )
      "find hex" in {
        hexOption.isDefined should be( true )
        hexOption.get.id should be( 28 )
        hexOption1.isDefined should be( true )
        hexOption1.get.id should be( 33 )
        hexOption2.isDefined should be( true )
        hexOption2.get.id should be( 22 )
        hexOption3.isDefined should be( true )
        hexOption3.get.id should be( 21 )
        hexOption4.isDefined should be( true )
        hexOption4.get.id should be( 27 )
      }
      val h = hexOption.get
      val h1 = hexOption1.get
      val h2 = hexOption2.get
      val h3 = hexOption3.get
      val h4 = hexOption4.get
      "should not find a hex" in {
        gameField.findHex( 7, 2 ).isDefined should be( false )
        gameField.findHex( 38 ).isDefined should be( false )
      }
      "find neighbours" in {
        gameField.adjacentHexes( h ) should be( Vector( h1, h2, h3, h4 ) )
        gameField.adjacentHex( h, 0 ) shouldBe Some( h1 )
        gameField.adjacentHex( h, 6 ) shouldBe None
      }
      "have edges" in {
        gameField.edges.size should be( 90 )
        val edge1 = gameField.findEdge( h, h1 )
        val edge2 = gameField.findEdge( (h2, h3) )
        edge1.isDefined should be( true )
        edge1.get.h1 should be( h )
        edge1.get.h2 should be( h1 )
        edge2.isDefined should be( true )
        edge2.get.h1 should be( h3 )
        edge2.get.h2 should be( h2 )
      }
      "have adjacent edges" in {
        val adjacent = gameField.adjacentEdges( h )
        edgeTuple( adjacent( 0 ) ) should be( (h, h1) )
        edgeTuple( adjacent( 1 ) ) should be( (h2, h) )
        edgeTuple( adjacent( 2 ) ) should be( (h3, h) )
        edgeTuple( adjacent( 3 ) ) should be( (h4, h) )
      }
      "have vertices" in {
        gameField.vertices.size should be( 54 )
        val vertex = gameField.findVertex( (h, h2, h3) )
        vertex.isDefined should be( true )
        vertex.get.h1 should be( h3 )
        vertex.get.h2 should be( h2 )
        vertex.get.h3 should be( h )
      }
      "have adjacent vertices" in {
        val adjacent = gameField.adjacentVertices( h )
        vertexTuple( adjacent( 0 ) ) should be( (h3, h2, h) )
        vertexTuple( adjacent( 1 ) ) should be( (h3, h4, h) )
        vertexTuple( adjacent( 2 ) ) should be( (h4, h, h1) )
      }
    }
  }

  def edgeTuple( e:Edge ):(Hex, Hex) = (e.h1, e.h2)

  def vertexTuple( v:Vertex ):(Hex, Hex, Hex) = (v.h1, v.h2, v.h3)
}
