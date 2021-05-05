package de.htwg.se.settlers.model

import de.htwg.se.settlers.model
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.Green
import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success}

class PlayerSpec extends WordSpec with Matchers {
  val player:Player = Player( new PlayerID( 0 ), Green, "A" )
  "PlayerColor" when {
    "static" should {
      "be constructed with of" in {
        Player.colorOf( "grEEn" ) shouldBe Some( Green )
        Player.colorOf( "Gree" ) shouldBe None
      }
      "have available colors" in {
        Player.availableColors() should contain theSameElementsAs Player.colors
        Player.availableColors( List( player ) ) should contain theSameElementsAs Player.colors.filter( _ != Green )
      }
    }
  }
  "Player" when {
    "new" should {
      "idName" in {
        player.idName shouldBe "<0>A"
      }
      "removeResourceCards" in {
        val p = player.copy( resources = ResourceCards.of( wood = 3, clay = 2 ) )
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
        player.addDevCard( KnightCard ).removeDevCard().devCards shouldBe empty
      }
      "addVictoryPoint" in {
        player.addVictoryPoint().victoryPoints shouldBe 1
      }
      "hasStructure" in {
        player.hasStructure( Road ) shouldBe true
        player.copy( structures = player.structures.updated( Road, 0 ) ).hasStructure( Road ) shouldBe false
      }
      "getStructure" in {
        player.copy( structures = player.structures.updated( Road, 0 ) ).getStructure( Road ) shouldBe Failure( InsufficientStructures( Road ) )
        val p1 = player.getStructure( Road )
        p1 shouldBe a [Success[_]]
        p1.get.structures( Road ) shouldBe Road.available - 1
        val p2 = player.getStructure( Settlement )
        p2 shouldBe a [Success[_]]
        p2.get.structures( Settlement ) shouldBe Settlement.available - 1
        val p3 = p2.get.getStructure( City )
        p3 shouldBe a [Success[_]]
        p3.get.structures( City ) shouldBe City.available - 1
        p3.get.structures( Settlement ) shouldBe Settlement.available
      }
      "addStructure" in {
        player.addStructure( Road ).structures( Road ) shouldBe Road.available + 1
        val p = player.addStructure( City )
        p.structures( Settlement ) shouldBe Settlement.available - 1
        p.structures( City ) shouldBe City.available + 1
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
