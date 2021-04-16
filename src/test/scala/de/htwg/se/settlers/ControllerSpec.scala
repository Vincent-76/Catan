package de.htwg.se.settlers

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Player.{Blue, Green, Red, Yellow}
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Success

/**
 * @author Vincent76;
 */
class ControllerSpec extends WordSpec with Matchers {
  "Controller" when {
    val controller = new Controller( test = true )
    "new" should {
      "have game" in {
        val observer = new Observer {
          override def onUpdate( info:Option[Info] ): Unit = ???
          override def onInfo(info: Info): Unit = ???
          override def onError(t: Throwable): Unit = ???
        }
        controller.add( observer )
        controller.remove( observer )
        controller.game.state shouldBe an[InitState]
        controller.hasUndo shouldBe false
        controller.hasRedo shouldBe false
      }
    }
    "running" should {
      "set InitPlayerState" in {
        controller.game.state.initPlayers()
        undoRedo( controller )
        controller.game.state shouldBe an[InitPlayerState]
      }
      "add players" in {
        controller.game.state.addPlayer( Green, "" )
        controller.game.state.addPlayer( Green, ( 0 to Game.maxPlayerNameLength ).map( _ => "A" ).mkString( "" ) )
        controller.game.state.addPlayer( Green, "A" )
        controller.game.state.addPlayer( Green, "B" )
        controller.game.state.addPlayer( Blue, "B" )
        controller.game.state.setInitBeginnerState()
        controller.game.state.addPlayer( Red, "B" )
        controller.game.state.addPlayer( Red, "C" )
        controller.game.state.addPlayer( Yellow, "D" )
        controller.undoAction()
        controller.game.players should have size 3
      }
      "set InitBeginnerState" in {
        controller.game.state.setInitBeginnerState()
        undoRedo( controller )
        controller.game.state shouldBe an[InitBeginnerState]
      }
      "dice out beginner" in {
        controller.game.state.diceOutBeginner()
        undoRedo( controller )
        controller.game.state.setBeginner()
        controller.game.state.diceOutBeginner()
        controller.game.state.diceOutBeginner()
        controller.game.state.setBeginner()
        undoRedo( controller )
        controller.player.id.id shouldBe 1
      }
      "build init" in {
        controller.game.state.buildInitSettlement( 100 )
        controller.game.state.buildInitSettlement( 21 )
        undoRedo( controller )
        controller.game.state.buildInitRoad( 100 )
        controller.game.state.buildInitRoad( 32 )
        undoRedo( controller )
        controller.game.state.buildInitSettlement( 21 )
        controller.game.state.buildInitSettlement( 32 )
        controller.game.state.buildInitRoad( 64 )
        controller.game.state.buildInitRoad( 67 )
        controller.game.state.buildInitSettlement( 35 )
        controller.game.state.buildInitRoad( 54 )
        undoRedo( controller )
        controller.game.state.buildInitSettlement( 5 )
        undoRedo( controller )
        controller.game.state.buildInitRoad( 9 )
        controller.game.state.buildInitSettlement( 2 )
        controller.game.state.buildInitRoad( 16 )
        undoRedo( controller )
        controller.game.state.buildInitSettlement( 0 )
        controller.game.state.buildInitRoad( 13 )
        undoRedo( controller )
        controller.game.state shouldBe a [NextPlayerState]
        getPlayer( controller, 0 ).resources shouldBe ResourceCards.of( wheat = 1 )
        getPlayer( controller, 1 ).resources shouldBe ResourceCards.of( wood = 1 )
        getPlayer( controller, 2 ).resources shouldBe ResourceCards.of( wood = 1, wheat = 1 )
        getPlayer( controller, 0 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
        getPlayer( controller, 1 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
        getPlayer( controller, 2 ).structures shouldBe Map( Road -> 13, Settlement ->3, City -> 4 )
      }
      "start turn" in {
        controller.game.state.startTurn()
        undoRedo( controller )
        controller.game.state shouldBe a [DiceState]
      }
      "roll dices" in {
        val player = getPlayer( controller, 2 )
        controller.game.state.rollTheDices() // 4 + 5 = 9
        undoRedo( controller )
        getPlayer( controller, 2 ).resources shouldBe player.resources.add( ResourceCards.of( wood = 1, clay = 1 ) )
        controller.game.state shouldBe a [ActionState]
      }
      "player trade" in {
        val player = getPlayer( controller )
        controller.game.state.setPlayerTradeState( ResourceCards.of( wood = 3 ), ResourceCards.of( wheat = 1 ) )
        controller.game.state.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( wheat = 1 ) )
        undoRedo( controller )
        controller.game.state.playerTradeDecision( false )
        undoRedo( controller )
        controller.game.state.playerTradeDecision( true )
        controller.game.state.abortPlayerTrade()
        undoRedo( controller )
        controller.game.state shouldBe a [ActionState]
        getPlayer( controller ).resources shouldBe player.resources
        controller.game.state.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( ore = 1 ) )
        controller.game.state.abortPlayerTrade()
        controller.game.state.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ) )
        controller.game.state.playerTradeDecision( true )
        controller.game.state.playerTrade( getPlayer( controller, 0 ).id )
        controller.game.state.playerTrade( getPlayer( controller, 2 ).id )
        undoRedo( controller )
        Success( getPlayer( controller ).resources ) shouldBe player.resources.add( Clay ).subtract( Wood )
      }
      "build road" in {
        val player = getPlayer( controller, 1 )
        controller.game.state.setBuildState( Road )
        undoRedo( controller )
        controller.game.state shouldBe a [BuildState]
        controller.game.state.build( 100 )
        controller.game.state.build( 32 )
        controller.game.state.build( 29 )
        controller.game.state.build( 0 )
        controller.game.state.build( 31 )
        undoRedo( controller )
        Success( getPlayer( controller, 1 ).resources ) shouldBe player.resources.subtract( Road.resources )
        getPlayer( controller, 1 ).structures.get( Road ) shouldBe player.structures.get( Road ).map( i => i - 1 )
        val edge = controller.game.gameField.findEdge( 31 )
        edge.isDefined shouldBe true
        edge.get.road.isDefined shouldBe true
        edge.get.road.get.owner shouldBe player.id
      }
      "end turn" in {
        controller.game.state.endTurn()
        undoRedo( controller )
        controller.game.state shouldBe a [NextPlayerState]
        controller.game.turn.playerID.id shouldBe 2
      }
      "let it run1" in {
        skipTurn( controller ) // 5 + 1 = 6
        skipTurn( controller ) // 3 + 3 = 6
        skipTurn( controller ) // 3 + 5 = 8
        skipTurn( controller ) // 6 + 5 = 11
        skipTurn( controller ) // 2 + 1 = 3
        skipTurn( controller ) // 5 + 3 = 8
        skipTurn( controller ) // 5 + 5 = 10
      }
      "place robber and steal from nobody" in {
        controller.game.state.startTurn()
        controller.game.state.rollTheDices() // 2 + 5 = 7
        controller.game.state.placeRobber( 50 )
        controller.game.state.placeRobber( 20 )
        controller.game.state.placeRobber( 1 )
        controller.game.state.placeRobber( 14 )
        undoRedo( controller )
        controller.game.gameField.robber.id shouldBe 14
        controller.game.state shouldBe a [ActionState]
      }
      "buy dev card" in {
        controller.game.state.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( sheep = 1 ) )
        controller.game.state.playerTradeDecision( true )
        controller.game.state.playerTrade( getPlayer( controller, 1 ).id )
        controller.game.state.buyDevCard()
        undoRedo( controller )
        getPlayer( controller ).devCards( 0 ) shouldBe GreatHallCard
        controller.game.state.endTurn()
      }
      "let it run2" in {
        skipTurn( controller ) // 4 + 1 = 5
        skipTurn( controller ) // 1 + 3 = 4
        skipTurn( controller ) // 1 + 3 = 4
      }
      "drop hand cards" in {
        controller.game.state.startTurn()
        controller.game.state.rollTheDices() // 5 + 5 = 10
        controller.game.state.setPlayerTradeState( ResourceCards.of(), ResourceCards.of( wheat = 2, ore = 1 ) )
        controller.game.state.playerTradeDecision( true )
        controller.game.state.playerTrade( getPlayer( controller, 0 ).id )
        controller.game.state.endTurn()
        controller.game.state.startTurn()
        controller.game.state.rollTheDices() // 6 + 1 = 7
        val playerC = getPlayer( controller, 2 )
        controller.game.state.dropResourceCardsToRobber( ResourceCards.of( wood = 1 ) )
        controller.game.state.dropResourceCardsToRobber( ResourceCards.of( ore = 4 ) )
        controller.game.state.dropResourceCardsToRobber( ResourceCards.of( wood = 2, wheat = 2 ) )
        undoRedo( controller )
        Success( getPlayer( controller, 2 ).resources ) shouldBe playerC.resources.subtract( ResourceCards.of( wood = 2, wheat = 2 ) )
        val playerB = getPlayer( controller, 1 )
        controller.game.state.dropResourceCardsToRobber( ResourceCards.of( wood = 1, sheep = 1, wheat = 2 ) )
        Success( getPlayer( controller, 1 ).resources ) shouldBe playerB.resources.subtract( ResourceCards.of( wood = 1, sheep = 1, wheat = 2 ) )
      }
      "place robber and steal from 1 nothing" in {
        controller.game.state.placeRobber( 27 )
      }







      "end" in {
        controller.undoAction()
        controller.game = controller.game.updatePlayer( controller.game.players.head._2.copy( victoryPoints = Game.requiredVictoryPoints ) )
        controller.redoAction()
      }
    }
  }

  private def undoRedo( controller:Controller ):Unit = {
    controller.undoAction()
    controller.redoAction()
  }

  private def getPlayer( controller:Controller, id:Int = -1 ):Player = {
    if( id >= 0 )
      controller.game.players( controller.game.getPlayerID( id ).get )
    else
      controller.game.player
  }

  private def skipTurn( controller:Controller ):Unit = {
    controller.game.state shouldBe a [NextPlayerState]
    controller.game.state.startTurn()
    controller.game.state.rollTheDices()
    controller.game.state.endTurn()
  }
}
