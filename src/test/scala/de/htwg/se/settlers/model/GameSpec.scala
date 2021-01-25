package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.ui.tui.tuistate.InitTUIState
import org.scalatest.{ Matchers, WordSpec }

/**
 * @author Vincent76;
 */
class GameSpec extends WordSpec with Matchers {
  "Game" when {
    val game = Game( InitState( null ) )
    "new" should {

    }
  }
}
