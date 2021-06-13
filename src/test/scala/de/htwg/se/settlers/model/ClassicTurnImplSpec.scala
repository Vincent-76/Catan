package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.impl.turn.ClassicTurnImpl
import org.scalatest.{ Matchers, WordSpec }

class ClassicTurnImplSpec extends WordSpec with Matchers {
  "Turn" when {
    val turn = ClassicTurnImpl( new PlayerID( 1 ) )
    "new" should {
      "have playerID" in {
        turn.playerID.id shouldBe 1
      }
      "manage dev cards" in {
        val turn2 = turn.addDrawnDevCard( KnightCard )
        turn2.drawnDevCards( KnightCard ) shouldBe 1
        turn2.removeDrawnDevCard().drawnDevCards( KnightCard ) shouldBe 0
        turn2.getLastDrawnDevCard shouldBe Some( KnightCard )
      }
    }
  }
}
