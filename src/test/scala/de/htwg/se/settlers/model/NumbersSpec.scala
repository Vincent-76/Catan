package de.htwg.se.settlers.model

import org.scalatest.{Matchers, WordSpec}

class NumbersSpec  extends WordSpec with Matchers {
  "Numbers" when {
    "static" should {
      "be constructed with of" in {
        Numbers.of( 1 ) shouldBe None
        Numbers.of( 2 ) shouldBe Some( Two )
        Numbers.of( 3 ) shouldBe Some( Three )
        Numbers.of( 4 ) shouldBe Some( Four )
        Numbers.of( 5 ) shouldBe Some( Five )
        Numbers.of( 6 ) shouldBe Some( Six )
        Numbers.of( 7 ) shouldBe Some( Seven )
        Numbers.of( 8 ) shouldBe Some( Eight )
        Numbers.of( 9 ) shouldBe Some( Nine )
        Numbers.of( 10 ) shouldBe Some( Ten )
        Numbers.of( 11 ) shouldBe Some( Eleven )
        Numbers.of( 12 ) shouldBe Some( Twelve )
        Numbers.of( 13 ) shouldBe None
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
