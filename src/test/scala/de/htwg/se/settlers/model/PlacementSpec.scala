package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.GameField.Hex
import de.htwg.se.settlers.model.Player.{Blue, Green}
import de.htwg.se.settlers.util._
import org.scalatest.{Matchers, WordSpec}

import scala.collection.immutable.TreeMap

class PlacementSpec extends WordSpec with Matchers {
  "Placement" when {
    val newGame = Game( test = true )
    "static" should {
      "be constructed with of" in {
        StructurePlacement.of( "rOaD" ) shouldBe Some( Road )
        StructurePlacement.of( "Roa" ) shouldBe None
      }
    }
    "Road" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        players = TreeMap(
          pID -> Player( pID, Green, "A" ),
          pID1 -> Player( pID1, Blue, "B" ),
        )( PlayerOrdering ),
      )
      "getBuildablePoints" in {
        val edge = game.gameField.findEdge( 58 ).get
        val edge1 = game.gameField.findEdge( 40 ).get
        val edge2 = game.gameField.findEdge( 41 ).get
        val edge3 = game.gameField.findEdge( 57 ).get
        val game2 = game.updateGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
          .update( edge2.setRoad( Some( Road( pID ) ) ) )
          .update( edge1.setRoad( Some( Road( pID1 ) ) ) ) )
        Road.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( edge3 )
      }
    }
    "Settlement" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        players = TreeMap(
          pID -> Player( pID, Green, "A" ),
          pID1 -> Player( pID1, Blue, "B" ),
        )( PlayerOrdering ),
      )
      "getBuildablePoints with any" in {
        val vertex = game.gameField.findVertex( 1 ).get
        val vertices = game.gameField.vertices.values
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val res = Settlement.getBuildablePoints( game2, pID, any = true )
        res should contain theSameElementsAs vertices.filter( v => !List( 0, 1, 2 ).contains( v.id ) )
      }
      "getBuildablePoints without any" in {
        val edge = game.gameField.findEdge( 1 ).get
        val vertex = game.gameField.findVertex( 0 ).get
        val vertex1 = game.gameField.findVertex( 2 ).get
        val game2 = game.updateGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
          .update( vertex1.setBuilding( Some( Settlement( pID1 ) ) ) ) )
        Settlement.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( vertex )
      }
    }
    "City" should {
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) ),
        turn = Turn( pID )
      )
      "getBuildablePoints" in {
        val vertex = game.gameField.vertices.head._2.setBuilding( Some( Settlement( pID ) ) )
        val game2 = game.updateGameField( game.gameField.update( vertex ) )
        City.getBuildablePoints( game2, pID ) should contain theSameElementsAs List( vertex )
      }
    }
    "Robber" should {
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) ),
        turn = Turn( pID )
      )
      "getBuildablePoints" in {
        val hexes = game.gameField.hexagons.red( List.empty[Hex], ( l:List[Hex], row:Vector[Option[Hex]] ) =>
          row.red( l, ( l:List[Hex], o:Option[Hex] ) => {
            if( o.isDefined && o.get.isLand && o.get != game.gameField.robber )
              l :+ o.get
            else l
          } )
        )
        Robber.getBuildablePoints( game, pID ) should contain theSameElementsAs hexes
      }
    }
  }
}
