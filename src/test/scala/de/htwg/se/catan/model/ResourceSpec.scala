package de.htwg.se.catan.model

import org.scalatest.{Matchers, WordSpec}

class ResourceSpec extends WordSpec with Matchers{
  "Resources" when {
    "static" should {
      "be constructed with of" in {
        Resource.of( "wOoD" ) shouldBe Some( Wood )
        Resource.of( "Woo" ) shouldBe None
      }
    }
    "new" should {
      "have a string representation" in {
        Wood.toString shouldBe Wood.title
      }
    }
  }
}
