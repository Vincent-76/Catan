package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import org.scalatest.{Matchers, WordSpec}

class CardSpec extends WordSpec with Matchers {
  "Cards" when {
    "DevCards" should {
      "find" in {
        Cards.usableDevCardOf( "kNight" ) shouldBe Some( KnightCard )
        Cards.usableDevCardOf( "Knigh" ) shouldBe None
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
    }
  }
}
