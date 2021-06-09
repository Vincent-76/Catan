package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.cards.KnightCard
import org.scalatest.{Matchers, WordSpec}

class TurnSpec extends WordSpec with Matchers {
  "Turn" when {
    val turn = Turn( new PlayerID( 1 ) )
    "new" should {
      "have playerID" in {
        turn.playerID.id shouldBe 1
      }
      "manage dev cards" in {
        val turn2 = turn.addDrawnDevCard( KnightCard )
        turn2.drawnDevCards shouldBe List( KnightCard )
        turn2.removeDrawnDevCard().drawnDevCards shouldBe empty
        turn2.getLastDrawnDevCard shouldBe Some( KnightCard )
      }
    }
  }
}
