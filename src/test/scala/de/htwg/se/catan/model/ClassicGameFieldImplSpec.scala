package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.util._
import org.scalatest.{ Matchers, WordSpec }

/**
 * @author Vincent76;
 */
class ClassicGameFieldImplSpec extends WordSpec with Matchers {
  "ClassicGameFieldImpl" when {
    "new" should {
      val gameField = ClassicGameFieldImpl( 1 )
      "create" in {
        ( 2 to 10 ).foreach( seed => ClassicGameFieldImpl( seed ).hexagons.red( 0, (i:Int, r:Vector[Option[Hex]] ) => {
          r.red( i, ( j:Int, h:Option[Hex] ) => j + ( if ( h.isDefined ) 1 else 0 ) )
        } ) should be( 37 ) )
      }
      "have a size" in {
        gameField.fieldWidth shouldBe 7
        gameField.fieldHeight shouldBe 7
        gameField.hexagons.red( 0, ( i:Int, r:Vector[Option[Hex]] ) => {
          r.red( i, ( j:Int, h:Option[Hex] ) => j + ( if ( h.isDefined ) 1 else 0 ) )
        } ) should be( 37 )
      }
      val hex = gameField.findHex( 4, 5 )
      val hex1 = gameField.findHex( 33 )
      val hex2 = gameField.findHex( 22 )
      val hex3 = gameField.findHex( 21 )
      val hex4 = gameField.findHex( 27 )
      "find hex" in {
        hex shouldNot be( None )
        hex.get.id should be( 28 )
        hex.get.isLand shouldBe false
        hex.get.isWater shouldBe true
        hex1 shouldNot be( None )
        hex1.get.id should be( 33 )
        hex2 shouldNot be( None )
        hex2.get.id should be( 22 )
        hex3 shouldNot be( None )
        hex3.get.id should be( 21 )
        hex3.get.isLand shouldBe true
        hex3.get.isWater shouldBe false
        hex4 shouldNot be( None )
        hex4.get.id should be( 27 )
      }
      val h = hex.get
      val h1 = hex1.get
      val h2 = hex2.get
      val h3 = hex3.get
      val h4 = hex4.get
      "not find hex" in {
        gameField.findHex( 7, 2 ) shouldBe None
        gameField.findHex( 38 ) shouldBe None
      }
      "find neighbours" in {
        gameField.adjacentHexes( h ) should be( Vector( h1, h2, h3, h4 ) )
        gameField.adjacentHex( h, 0 ) shouldBe Some( h1 )
      }
      "not find neighbours" in {
        gameField.adjacentHex( h, 6 ) shouldBe None
      }
      "have adjacent players" in {
        gameField.adjacentPlayers( h ) shouldBe empty
        val pID = new PlayerID( 1 )
        val vertex = gameField.findVertex( 46 )
        vertex shouldNot be( None )
        gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ).adjacentPlayers( h ) should contain theSameElementsAs List( pID )
      }
      "have edges" in {
        gameField.edges.size should be( 90 )
      }
      val edge = gameField.findEdge( 59 )
      val edge1 = gameField.findEdge( h, h3 )
      val edge2 = gameField.findEdge( (h2, h3) )
      "find edge" in {
        edge shouldNot be( None )
        edge.get.id shouldBe 59
        edge.get.h1 should be( h2 )
        edge.get.h2 should be( h )
        edge1 shouldNot be( None )
        edge1.get.id shouldBe 57
        edge1.get.h1 should be( h3 )
        edge1.get.h2 should be( h )
        edge2 shouldNot be( None )
        edge2.get.id shouldBe 58
        edge2.get.h1 should be( h3 )
        edge2.get.h2 should be( h2 )
      }
      val e = edge.get
      val e1 = edge1.get
      val e2 = edge2.get
      "not find edge" in {
        gameField.findEdge( 90 ) shouldBe None
        gameField.findEdge( h1, Hex( 100, 0, 0, DesertArea ) ) shouldBe None
      }
      "update edge" in {
        val pID = new PlayerID( 0 )
        val gameField2 = gameField.update( e.setRoad( Some( Road( pID ) ) ) )
        val nEdge = gameField2.findEdge( 59 )
        nEdge shouldNot be( None )
        nEdge.get.road shouldNot be( None )
        nEdge.get.road.get.owner shouldBe pID
      }
      "have vertices" in {
        gameField.vertices.size should be( 54 )
      }
      val vertex = gameField.findVertex( 37 )
      val vertex1 = gameField.findVertex( h, h3, h4 )
      val vertex2 = gameField.findVertex( (h, h4, h1) )
      "find vertex" in {
        vertex shouldNot be( None )
        vertex.get.id shouldBe 37
        vertex.get.h1 should be( h3 )
        vertex.get.h2 should be( h2 )
        vertex.get.h3 should be( h )
        vertex1 shouldNot be( None )
        vertex1.get.id shouldBe 36
        vertex1.get.h1 should be( h3 )
        vertex1.get.h2 should be( h4 )
        vertex1.get.h3 should be( h )
        vertex2 shouldNot be( None )
        vertex2.get.id shouldBe 46
        vertex2.get.h1 should be( h4 )
        vertex2.get.h2 should be( h )
        vertex2.get.h3 should be( h1 )
      }
      val v = vertex.get
      val v1 = vertex1.get
      val v2 = vertex2.get
      "not find vertex" in {
        gameField.findVertex( 54 ) shouldBe None
        gameField.findVertex( h1, h2, Hex( 100, 0, 0, DesertArea ) ) shouldBe None
      }
      "update vertex" in {
        val pID = new PlayerID( 0 )
        val gameField2 = gameField.update( v.setBuilding( Some( Settlement( pID ) ) ) )
        val nVertex = gameField2.findVertex( 37 )
        nVertex shouldNot be( None )
        nVertex.get.building shouldNot be( None )
        nVertex.get.building.get.owner shouldBe pID
      }
      "have hex adjacent edges" in {
        val adjacentF = gameField.adjacentEdge( h, ClassicGameFieldImpl.adjacentOffset.size )
        adjacentF shouldBe None
        val adjacent0 = gameField.adjacentEdge( h, 0 )
        adjacent0 shouldNot be( None )
        edgeTuple( adjacent0.get ) shouldBe (h, h1)
        val adjacent = gameField.adjacentEdges( h )
        edgeTuple( adjacent.head ) shouldBe (h, h1)
        adjacent( 1 ) shouldBe e
        adjacent( 2 ) shouldBe e1
        edgeTuple( adjacent( 3 ) ) shouldBe (h4, h)
      }
      "have hex adjacent vertices" in {
        val adjacent = gameField.adjacentVertices( h )
        adjacent.head shouldBe v
        adjacent( 1 ) shouldBe v1
        adjacent( 2 ) shouldBe v2
      }
      "have edge adjacent hexes" in {
        gameField.adjacentHexes( e ) should contain theSameElementsAs List( h, h2 )
      }
      "have edge adjacent edges" in {
        gameField.adjacentEdges( e ) should contain theSameElementsAs List( e1, e2 )
      }
      "have edge adjacent vertices" in {
        gameField.adjacentVertices( e ) should contain theSameElementsAs List( v )
        gameField.adjacentVertices( e1 ) should contain theSameElementsAs List( v, v1 )
      }
      "have vertex adjacent edges" in {
        gameField.adjacentEdges( v ) should contain theSameElementsAs List( e, e1, e2 )
      }
    }
  }

  def edgeTuple( e:Edge ):(Hex, Hex) = (e.h1, e.h2)

  def vertexTuple( v:Vertex ):(Hex, Hex, Hex) = (v.h1, v.h2, v.h3)
}
