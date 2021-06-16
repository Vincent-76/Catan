package de.htwg.se.catan.model

import Cards.ResourceCards
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import org.scalatest.{ Matchers, WordSpec }

import scala.util.{ Failure, Success }

class ClassicPlayerImplSpec extends WordSpec with Matchers {
  val player:ClassicPlayerImpl = ClassicPlayerImpl( new PlayerID( 0 ), Green, "A" )
  "PlayerColor" when {
    "static" should {
      "be constructed with of" in {
        PlayerColor.colorOf( "grEEn" ) shouldBe Some( Green )
        PlayerColor.colorOf( "Gree" ) shouldBe None
      }
      "have available colors" in {
        PlayerColor.availableColors() should contain theSameElementsAs PlayerColor.all
        PlayerColor.availableColors( List( player.color ) ) should contain theSameElementsAs PlayerColor.all.filter( _ != Green )
      }
    }
  }
  "Player" when {
    "new" should {
      "idName" in {
        player.idName shouldBe "<0>A"
      }
      "removeResourceCards" in {
        val p = player.copy( resourcesVal = ResourceCards.of( wood = 3, clay = 2 ) )
        val p2 = p.removeResourceCard( Wood )
        p2 shouldBe a [Success[Player]]
        p2.get.resources shouldBe ResourceCards.of( wood = 2, clay = 2 )
        p2.get.removeResourceCard( Clay, 3 ) shouldBe Failure( InsufficientResources )
        val p3 = p.removeResourceCards( ResourceCards.of( wood = 2 ) )
        p3 shouldBe a [Success[Player]]
        p3.get.resources shouldBe ResourceCards.of( wood = 1, clay = 2 )
        p3.get.removeResourceCards( ResourceCards.of( clay = 2, wood = 2 ) ) shouldBe Failure( InsufficientResources )
      }
      "addResourceCards" in {
        val p = player.addResourceCard( Wood )
        p.resources shouldBe ResourceCards.of( wood = 1 )
        p.addResourceCard( Clay, 2 ).resources shouldBe ResourceCards.of( wood = 1, clay = 2 )
        p.addResourceCards( ResourceCards.of( wood = 2, clay = 1 ) ).resources shouldBe ResourceCards.of( wood = 3, clay = 1 )
      }
      "trade" in {
        val p = player.addResourceCards( ResourceCards.of( wood = 2, clay = 2 ) )
        p.trade( ResourceCards.of( wheat = 1, ore = 2 ), ResourceCards.of( wood = 1, clay = 3 ) ) shouldBe Failure( InsufficientResources )
        val p2 = p.trade( ResourceCards.of( wheat = 1, ore = 2 ), ResourceCards.of( wood = 1, clay = 2 ) )
        p2 shouldBe a [Success[Player]]
        p2.get.resources shouldBe ResourceCards.of( wood = 1, wheat = 1, ore = 2 )
      }
      "addDevCard" in {
        player.addDevCard( KnightCard ).devCards shouldBe Vector( KnightCard )
      }
      "removeDevCard" in {
        player.addDevCard( KnightCard ).removeLastDevCard().devCards shouldBe empty
      }
      "addVictoryPoint" in {
        player.addVictoryPoint().victoryPoints shouldBe 1
      }
      "hasStructure" in {
        player.hasStructure( RoadPlacement ) shouldBe true
        player.copy( structures = player.structures.updated( RoadPlacement, 0 ) ).hasStructure( RoadPlacement ) shouldBe false
      }
      "getStructure" in {
        player.copy( structures = player.structures.updated( RoadPlacement, 0 ) ).getStructure( RoadPlacement ) shouldBe Failure( InsufficientStructures( RoadPlacement ) )
        val p1 = player.getStructure( RoadPlacement )
        p1 shouldBe a [Success[_]]
        p1.get.structures( RoadPlacement ) shouldBe RoadPlacement.available - 1
        val p2 = player.getStructure( SettlementPlacement )
        p2 shouldBe a [Success[_]]
        p2.get.structures( SettlementPlacement ) shouldBe SettlementPlacement.available - 1
        val p3 = p2.get.getStructure( CityPlacement )
        p3 shouldBe a [Success[_]]
        p3.get.structures( CityPlacement ) shouldBe CityPlacement.available - 1
        p3.get.structures( SettlementPlacement ) shouldBe SettlementPlacement.available
      }
      "addStructure" in {
        player.addStructure( RoadPlacement ).structures( RoadPlacement ) shouldBe RoadPlacement.available + 1
        val p = player.addStructure( CityPlacement )
        p.structures( SettlementPlacement ) shouldBe SettlementPlacement.available - 1
        p.structures( CityPlacement ) shouldBe CityPlacement.available + 1
      }
      "randomHandResource" in {
        player.randomHandResource() shouldBe None
        val r = player.addResourceCards( ResourceCards.of( wood = 1, wheat = 2, ore = 1 ) ).randomHandResource()
        r shouldNot be( None )
        r.get should ( be( Wood ) or be( Wheat ) or be( Ore ) )
      }
      "useDevCard" in {
        player.useDevCard( KnightCard ) shouldBe Failure( InsufficientDevCards( KnightCard ) )
        val p = player.addDevCard( MonopolyCard ).useDevCard( MonopolyCard )
        p shouldBe a [Success[_]]
        p.get.devCards shouldBe empty
        p.get.usedDevCards should contain( MonopolyCard )
      }
    }
  }
}
