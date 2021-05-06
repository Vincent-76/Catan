package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Player.{Blue, Green, Red, Yellow}
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state._
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
        undoRedo( controller )
        controller.endTurn()
      }
    }
    "running" should {
      "init players" in {
        controller.initPlayers()
        undoRedo( controller )
        controller.addPlayer( Green, "A" )
        controller.addPlayer( Blue, "A" )
        controller.addPlayer( Blue, "B" )
        controller.addPlayer( Yellow, "C" )
        controller.setInitBeginnerState()
        controller.diceOutBeginner()
        controller.diceOutBeginner()
        controller.setBeginner()
        controller.onTurn.id shouldBe 1
        controller.player.id.id shouldBe 1
        controller.player( controller.onTurn ).id.id shouldBe 1
        val oldGame = controller.game
        val p = controller.player.copy( victoryPoints = Game.requiredVictoryPoints )
        controller.game = controller.game.updatePlayer( p )
        controller.buildInitSettlement( 21 )
        controller.game.winner shouldBe Some( p.id )
        controller.running shouldBe false
        controller.undoAction()
        controller.game = oldGame
        ( 1 to 8 ).foreach( _ => controller.undoAction() )
      }
      "call state methods" in {
        controller.addPlayer( Green, "A" )
        controller.setInitBeginnerState()
        controller.initPlayers()
        controller.initPlayers()
        controller.diceOutBeginner()
        controller.setBeginner()
        controller.buildInitSettlement( 0 )
        controller.buildInitRoad( 0 )
        controller.startTurn()
        controller.rollTheDices()
        controller.useDevCard( KnightCard )
        controller.dropResourceCardsToRobber( ResourceCards.of() )
        controller.placeRobber( 0 )
        controller.robberStealFromPlayer( controller.onTurn )
        controller.setBuildState( Road )
        controller.build( 0 )
        controller.bankTrade( ResourceCards.of(), ResourceCards.of() )
        controller.setPlayerTradeState( ResourceCards.of(), ResourceCards.of() )
        controller.playerTradeDecision( false )
        controller.abortPlayerTrade()
        controller.playerTrade( controller.onTurn )
        controller.buyDevCard()
        controller.yearOfPlentyAction( ResourceCards.of() )
        controller.devBuildRoad( 0 )
        controller.monopolyAction( Wood )
        controller.endTurn()
      }
    }
    /*"running" should {
      "set InitPlayerState" in {
        controller.initPlayers()
        undoRedo( controller )
        controller.game.state shouldBe an[InitPlayerState]
      }
      "add players" in {
        controller.addPlayer( Green, "" )
        controller.addPlayer( Green, ( 0 to Game.maxPlayerNameLength ).map( _ => "A" ).mkString( "" ) )
        controller.addPlayer( Green, "A" )
        controller.addPlayer( Green, "B" )
        controller.addPlayer( Blue, "B" )
        controller.setInitBeginnerState()
        controller.addPlayer( Red, "B" )
        controller.addPlayer( Red, "C" )
        controller.addPlayer( Yellow, "D" )
        controller.undoAction()
        controller.game.players should have size 3
      }
      "set InitBeginnerState" in {
        controller.setInitBeginnerState()
        undoRedo( controller )
        controller.game.state shouldBe an[InitBeginnerState]
      }
      "dice out beginner" in {
        controller.diceOutBeginner()
        undoRedo( controller )
        controller.setBeginner()
        controller.diceOutBeginner()
        controller.diceOutBeginner()
        controller.setBeginner()
        undoRedo( controller )
        controller.player.id.id shouldBe 1
      }
      "build init" in {
        controller.buildInitSettlement( 100 )
        controller.buildInitSettlement( 21 )
        undoRedo( controller )
        controller.buildInitRoad( 100 )
        controller.buildInitRoad( 32 )
        undoRedo( controller )
        controller.buildInitSettlement( 21 )
        controller.buildInitSettlement( 32 )
        controller.buildInitRoad( 64 )
        controller.buildInitRoad( 67 )
        controller.buildInitSettlement( 35 )
        controller.buildInitRoad( 54 )
        undoRedo( controller )
        controller.buildInitSettlement( 5 )
        undoRedo( controller )
        controller.buildInitRoad( 9 )
        controller.buildInitSettlement( 2 )
        controller.buildInitRoad( 16 )
        undoRedo( controller )
        controller.buildInitSettlement( 0 )
        controller.buildInitRoad( 13 )
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
        controller.startTurn()
        undoRedo( controller )
        controller.game.state shouldBe a [DiceState]
      }
      "roll dices" in {
        val player = getPlayer( controller, 2 )
        controller.rollTheDices() // 4 + 5 = 9
        undoRedo( controller )
        getPlayer( controller, 2 ).resources shouldBe player.resources.add( ResourceCards.of( wood = 1, clay = 1 ) )
        controller.game.state shouldBe a [ActionState]
      }
      "player trade" in {
        val player = getPlayer( controller )
        controller.setPlayerTradeState( ResourceCards.of( wood = 3 ), ResourceCards.of( wheat = 1 ) )
        controller.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( wheat = 1 ) )
        undoRedo( controller )
        controller.playerTradeDecision( false )
        undoRedo( controller )
        controller.playerTradeDecision( true )
        controller.abortPlayerTrade()
        undoRedo( controller )
        controller.game.state shouldBe a [ActionState]
        getPlayer( controller ).resources shouldBe player.resources
        controller.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( ore = 1 ) )
        controller.abortPlayerTrade()
        controller.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ) )
        controller.playerTradeDecision( true )
        controller.playerTrade( getPlayer( controller, 0 ).id )
        controller.playerTrade( getPlayer( controller, 2 ).id )
        undoRedo( controller )
        Success( getPlayer( controller ).resources ) shouldBe player.resources.add( Clay ).subtract( Wood )
      }
      "build road" in {
        val player = getPlayer( controller, 1 )
        controller.setBuildState( Road )
        undoRedo( controller )
        controller.game.state shouldBe a [BuildState]
        controller.build( 100 )
        controller.build( 32 )
        controller.build( 29 )
        controller.build( 0 )
        controller.build( 31 )
        undoRedo( controller )
        Success( getPlayer( controller, 1 ).resources ) shouldBe player.resources.subtract( Road.resources )
        getPlayer( controller, 1 ).structures.get( Road ) shouldBe player.structures.get( Road ).map( i => i - 1 )
        val edge = controller.game.gameField.findEdge( 31 )
        edge.isDefined shouldBe true
        edge.get.road.isDefined shouldBe true
        edge.get.road.get.owner shouldBe player.id
      }
      "end turn" in {
        controller.endTurn()
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
        controller.startTurn()
        controller.rollTheDices() // 2 + 5 = 7
        controller.placeRobber( 50 )
        controller.placeRobber( 20 )
        controller.placeRobber( 1 )
        controller.placeRobber( 14 )
        undoRedo( controller )
        controller.game.gameField.robber.id shouldBe 14
        controller.game.state shouldBe a [ActionState]
      }
      "buy dev card" in {
        controller.setPlayerTradeState( ResourceCards.of( wood = 1 ), ResourceCards.of( sheep = 1 ) )
        controller.playerTradeDecision( true )
        controller.playerTrade( getPlayer( controller, 1 ).id )
        controller.buyDevCard()
        undoRedo( controller )
        getPlayer( controller ).devCards( 0 ) shouldBe GreatHallCard
        controller.endTurn()
      }
      "let it run2" in {
        skipTurn( controller ) // 4 + 1 = 5
        skipTurn( controller ) // 1 + 3 = 4
        skipTurn( controller ) // 1 + 3 = 4
      }
      "drop hand cards" in {
        controller.startTurn()
        controller.rollTheDices() // 5 + 5 = 10
        controller.setPlayerTradeState( ResourceCards.of(), ResourceCards.of( wheat = 2, ore = 1 ) )
        controller.playerTradeDecision( true )
        controller.playerTrade( getPlayer( controller, 0 ).id )
        controller.endTurn()
        controller.startTurn()
        controller.rollTheDices() // 6 + 1 = 7
        val playerC = getPlayer( controller, 2 )
        controller.dropResourceCardsToRobber( ResourceCards.of( wood = 1 ) )
        controller.dropResourceCardsToRobber( ResourceCards.of( ore = 4 ) )
        controller.dropResourceCardsToRobber( ResourceCards.of( wood = 2, wheat = 2 ) )
        undoRedo( controller )
        Success( getPlayer( controller, 2 ).resources ) shouldBe playerC.resources.subtract( ResourceCards.of( wood = 2, wheat = 2 ) )
        val playerB = getPlayer( controller, 1 )
        controller.dropResourceCardsToRobber( ResourceCards.of( wood = 1, sheep = 1, wheat = 2 ) )
        Success( getPlayer( controller, 1 ).resources ) shouldBe playerB.resources.subtract( ResourceCards.of( wood = 1, sheep = 1, wheat = 2 ) )
      }
      "place robber and steal from 1 nothing" in {
        controller.placeRobber( 27 )
      }







      "end" in {
        controller.undoAction()
        controller.game = controller.game.updatePlayer( controller.game.players.head._2.copy( victoryPoints = Game.requiredVictoryPoints ) )
        controller.redoAction()
      }
    }*/
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
    controller.startTurn()
    controller.rollTheDices()
    controller.endTurn()
  }
}
