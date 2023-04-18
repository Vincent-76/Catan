package de.htwg.se.catan.model

import de.htwg.se.catan.CatanModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DiceValuesSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "Numbers" when {
    "static" should {
      "be constructed with of" in {
        DiceValue.of( 1 ) shouldBe None
        DiceValue.of( 2 ) shouldBe Some( DiceValue.Two )
        DiceValue.of( 3 ) shouldBe Some( DiceValue.Three )
        DiceValue.of( 4 ) shouldBe Some( DiceValue.Four )
        DiceValue.of( 5 ) shouldBe Some( DiceValue.Five )
        DiceValue.of( 6 ) shouldBe Some( DiceValue.Six )
        DiceValue.of( 7 ) shouldBe Some( DiceValue.Seven )
        DiceValue.of( 8 ) shouldBe Some( DiceValue.Eight )
        DiceValue.of( 9 ) shouldBe Some( DiceValue.Nine )
        DiceValue.of( 10 ) shouldBe Some( DiceValue.Ten )
        DiceValue.of( 11 ) shouldBe Some( DiceValue.Eleven )
        DiceValue.of( 12 ) shouldBe Some( DiceValue.Twelve )
        DiceValue.of( 13 ) shouldBe None
      }
    }
    "created" should {
      "have a string representation" in {
        DiceValue.Two.toString shouldBe "02"
        DiceValue.Eleven.toString shouldBe "11"
      }
    }
  }

}
