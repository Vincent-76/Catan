package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.util._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlacementSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "Placement" when {
    "static" should {
      "be constructed with of" in {
        StructurePlacement.of( "rOaD" ) shouldBe Some( RoadPlacement )
        StructurePlacement.of( "Roa" ) shouldBe None
      }
    }
    val game = new ClassicGameImpl( ClassicGameFieldImpl( 1 ), ClassicTurnImpl(), 1, ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ), "ClassicPlayerImpl" )
      .addPlayer( Green, "A" )
      .addPlayer( Blue, "B" )
      .addPlayer( Yellow, "C" )
      .use( g => g.setTurn( g.turn.set( g.getPlayerID( 0 ).get ) ) )
    val pID = game.getPlayerID( 0 ).get
    val pID1 = game.getPlayerID( 1 ).get
    "Road" should {
      "getBuildablePoints" in {
        val edge = game.gameField.findEdge( 58 ).get
        val edge1 = game.gameField.findEdge( 40 ).get
        val edge2 = game.gameField.findEdge( 41 ).get
        val edge3 = game.gameField.findEdge( 57 ).get
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
          .update( edge2.setRoad( Some( Road( pID ) ) ) )
          .update( edge1.setRoad( Some( Road( pID1 ) ) ) ) )
        RoadPlacement.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( edge3 )
      }
    }
    "Settlement" should {
      "getBuildablePoints with any" in {
        val vertex = game.gameField.findVertex( 1 ).get
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val res = SettlementPlacement.getBuildablePoints( game2, pID, any = true )
        res should contain theSameElementsAs game.gameField.vertexList.filter( v => !List( 0, 1, 2 ).contains( v.id ) )
      }
      "getBuildablePoints without any" in {
        val edge = game.gameField.findEdge( 1 ).get
        val vertex = game.gameField.findVertex( 0 ).get
        val vertex1 = game.gameField.findVertex( 2 ).get
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
          .update( vertex1.setBuilding( Some( Settlement( pID1 ) ) ) ) )
        SettlementPlacement.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( vertex )
      }
    }
    "City" should {
      "getBuildablePoints" in {
        val vertex = game.gameField.vertexList.head.setBuilding( Some( Settlement( pID ) ) )
        val game2 = game.setGameField( game.gameField.update( vertex ) )
        CityPlacement.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( vertex )
      }
    }
    "Robber" should {
      "getBuildablePoints" in {
        val hexes = game.gameFieldVal.hexList.filter( h => h.isLand && h != game.gameField.robberHex )
        RobberPlacement.getBuildablePoints( game, pID ) should contain theSameElementsAs hexes
      }
    }
  }
}
