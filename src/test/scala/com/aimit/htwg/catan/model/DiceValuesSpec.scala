package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.CatanModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DiceValuesSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "Numbers" when {
    "static" should {
      "be constructed with of" in {
        DiceValue.of( 1 ) shouldBe None
        DiceValue.of( 2 ) shouldBe Some( Two )
        DiceValue.of( 3 ) shouldBe Some( Three )
        DiceValue.of( 4 ) shouldBe Some( Four )
        DiceValue.of( 5 ) shouldBe Some( Five )
        DiceValue.of( 6 ) shouldBe Some( Six )
        DiceValue.of( 7 ) shouldBe Some( Seven )
        DiceValue.of( 8 ) shouldBe Some( Eight )
        DiceValue.of( 9 ) shouldBe Some( Nine )
        DiceValue.of( 10 ) shouldBe Some( Ten )
        DiceValue.of( 11 ) shouldBe Some( Eleven )
        DiceValue.of( 12 ) shouldBe Some( Twelve )
        DiceValue.of( 13 ) shouldBe None
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
