package de.htwg.se.catan.model

import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClassicTurnImplSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "Turn" when {
    val turn = new ClassicTurnImpl()
    "new" should {
      "have playerID" in {
        turn.playerID.id shouldBe -1
      }
      "manage dev cards" in {
        val turn2 = turn.addDrawnDevCard( KnightCard )
        turn2.drawnDevCards should have size 1
        turn2.removeDrawnDevCard().drawnDevCards shouldBe empty
        turn2.getLastDrawnDevCard shouldBe Some( KnightCard )
      }
    }
  }
}
