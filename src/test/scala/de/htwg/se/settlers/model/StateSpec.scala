package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.state.InitState
import org.scalatest.{Matchers, WordSpec}

class StateSpec extends WordSpec with Matchers {
  "State" when {
    val game = Game( InitState(), test = true )
    "InitState" should {
      "initPlayers" in {
        game.state.initPlayers()
      }
    }
  }
}
