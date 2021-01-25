package de.htwg.se.settlers

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Player.{ Blue, Green, Red, Yellow }
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._
import org.scalatest.{ Matchers, WordSpec }

/**
 * @author Vincent76;
 */
class ControllerSpec extends WordSpec with Matchers {
  "Controller" when {
    val controller = new Controller( true )
    "new" should {
      "have game" in {
        controller.game.state shouldBe an[InitState]
      }
    }
    /*"running" should {
      "set InitPlayerState" in {
        controller.game.state.initPlayers()
        controller.undoAction()
        controller.redoAction()
        controller.game.state shouldBe an[InitPlayerState]
      }
      "add players" in {
        controller.game.state.addPlayer( Green, "A" )
        controller.game.state.addPlayer( Green, "B" )
        controller.game.state.addPlayer( Blue, "B" )
        controller.game.state.setInitBeginnerState()
        controller.game.state.addPlayer( Blue, "C" )
        controller.game.state.addPlayer( Red, "C" )
        controller.game.state.addPlayer( Yellow, "D" )
        controller.undoAction()
        controller.game.players should have size 3
      }
      "set InitBeginnerState" in {
        controller.game.state.setInitBeginnerState()
        controller.undoAction()
        controller.redoAction()
        controller.game.state shouldBe an[InitBeginnerState]
      }
      "dice out beginner" in {
        controller.game.state.diceOutBeginner()
        controller.game.state.diceOutBeginner()
        controller.player.id.id should be( 1 )
      }
      "build init" in {
        controller.game.state.buildInitSettlement( 30 )
        controller.undoAction()
        controller.redoAction()
        controller.game.state.buildInitRoad( 64 )
        controller.undoAction()
        controller.redoAction()
        controller.game.state.buildInitSettlement( 30 )
        controller.game.state.buildInitSettlement( 32 )
        controller.game.state.buildInitRoad( 64 )
        controller.game.state.buildInitRoad( 67 )
        controller.game.state.buildInitSettlement( 34 )
        controller.game.state.buildInitRoad( 70 )
        controller.undoAction()
        controller.redoAction()
        controller.game.state.buildInitSettlement( 5 )
        controller.game.state.buildInitRoad( 9 )
        controller.game.state.buildInitSettlement( 3 )
        controller.game.state.buildInitRoad( 6 )
        controller.undoAction()
        controller.redoAction()
        controller.game.state.buildInitSettlement( 1 )
        controller.game.state.buildInitRoad( 3 )
        controller.undoAction()
        controller.redoAction()
        controller.game.state shouldBe a [NextPlayerState]
        getPlayer( controller, 0 ).resources shouldBe ResourceCards.of( wheat = 1 )
        getPlayer( controller, 1 ).resources shouldBe ResourceCards.of( wood = 1 )
        getPlayer( controller, 2 ).resources shouldBe ResourceCards.of( wheat = 1 )
        getPlayer( controller, 0 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
        getPlayer( controller, 1 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
        getPlayer( controller, 2 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
      }
      "set DiceSate" in {
        controller.game.state.startTurn()
        controller.game.state shouldBe a [DiceState]
      }
      "roll dices" in {
        val player = controller.player
        controller.game = controller.game.updatePlayer( player.copy( resources = player.resources.add( Wood, 10 ) ) )
        controller.game.state.rollTheDices()
      }
      "undo" in {
        controller.undoAction()
        controller.undoAction()
      }
      "redo" in {
        controller.redoAction()
        controller.redoAction()
      }
      "end" in {
        controller.undoAction()
        controller.game = controller.game.updatePlayer( controller.game.players.head._2.copy( victoryPoints = Game.requiredVictoryPoints ) )
        controller.redoAction()
      }
    }
    "commands" should {

    }*/
  }

  private def undoRedo( controller:Controller ):Unit = {
    controller.undoAction()
    controller.redoAction()
  }

  private def getPlayer( controller:Controller, id:Int ):Player =
    controller.game.players( controller.game.getPlayerID( id ).get )
}
