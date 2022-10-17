package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.CatanModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ResourceSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
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
