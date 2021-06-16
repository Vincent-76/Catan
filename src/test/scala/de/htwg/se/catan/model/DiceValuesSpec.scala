package de.htwg.se.catan.model

import org.scalatest.{Matchers, WordSpec}

class DiceValuesSpec  extends WordSpec with Matchers {
  "Numbers" when {
    "static" should {
      "be constructed with of" in {
        DiceValues.of( 1 ) shouldBe None
        DiceValues.of( 2 ) shouldBe Some( Two )
        DiceValues.of( 3 ) shouldBe Some( Three )
        DiceValues.of( 4 ) shouldBe Some( Four )
        DiceValues.of( 5 ) shouldBe Some( Five )
        DiceValues.of( 6 ) shouldBe Some( Six )
        DiceValues.of( 7 ) shouldBe Some( Seven )
        DiceValues.of( 8 ) shouldBe Some( Eight )
        DiceValues.of( 9 ) shouldBe Some( Nine )
        DiceValues.of( 10 ) shouldBe Some( Ten )
        DiceValues.of( 11 ) shouldBe Some( Eleven )
        DiceValues.of( 12 ) shouldBe Some( Twelve )
        DiceValues.of( 13 ) shouldBe None
      }
    }
    "created" should {
      "have a string representation" in {
        Two.toString shouldBe "02"
        Eleven.toString shouldBe "11"
      }
    }
  }

}
