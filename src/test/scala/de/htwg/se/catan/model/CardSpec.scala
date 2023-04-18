package de.htwg.se.catan.model

import de.htwg.se.catan.model.error.InsufficientResources
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.CatanModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{ Failure, Success }

class CardSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "Cards" when {
    "DevCards" should {
      "find" in {
        DevelopmentCard.usableOf( "kNight" ) shouldBe Some( KnightCard )
        DevelopmentCard.usableOf( "Knigh" ) shouldBe None
      }
      "have a title" in {
        KnightCard.toString shouldBe KnightCard.title
      }
    }
    "ResourceCards" should {
      "construct with of" in {
        ResourceCards.of( wood = 1, clay = 2, sheep = 3, wheat = 4, ore = 5 ) shouldBe
          Map( Wood -> 1, Clay -> 2, Sheep -> 3, Wheat -> 4, Ore -> 5 )
      }
      "construct with ofResource" in {
        ResourceCards.ofResource( Wood, 5 ) shouldBe Map( Wood -> 5 )
      }
      val cards = ResourceCards.of( 4, 2, 3, 7, 1 )
      "add" in {
        cards.add( Clay, 2 ) shouldBe ResourceCards.of( 4, 4, 3, 7, 1 )
        cards.add( ResourceCards.of( 4, 2 ) ) shouldBe
          ResourceCards.of( 8, 4, 3, 7, 1 )
      }
      "subtract" in {
        cards.subtract( Wheat, 7 ) shouldBe
          Success( ResourceCards.of( 4, 2, 3, 0, 1 ) )
        cards.subtract( Clay, 3 ) shouldBe Failure( InsufficientResources )
        cards.subtract( ResourceCards.of( 2 ) ) shouldBe
          Success( ResourceCards.of( 2, 2, 3, 7, 1 ) )
        ResourceCards.ofResource( Wood, 2 ).subtract( ResourceCards.of( clay = 5 ) ) shouldBe Failure( InsufficientResources )
      }
      "amount" in {
        cards.amount shouldBe 17
      }
      "has" in {
        cards.has( ResourceCards.of( 2, 1, 3, 5, 1 ) ) shouldBe true
      }
      "sort" in {
        cards.sort shouldBe Seq( (Wood, 4), (Clay, 2), (Sheep, 3), (Wheat, 7), (Ore, 1) )
      }
      "toString" in {
        cards.toString( "+" ) shouldBe "+2 Clay, +3 Sheep, +7 Wheat, +1 Ore, +4 Wood"
      }
    }
  }
}
