package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ KnightCard, Road, _ }
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.util._
import org.scalatest.{ Matchers, WordSpec }

/**
 * @author Vincent76;
 */
class ControllerSpec extends WordSpec with Matchers {
  class TestObserver extends Observer {
    var updateInfo:Option[Info] = None
    var info:Option[Info] = None
    var error:Option[Throwable] = None

    override def onUpdate(info: Option[Info]): Unit = this.updateInfo = info

    override def onInfo(info: Info): Unit = this.info = Some( info )

    override def onError(t: Throwable): Unit = this.error = Some( t )
  }

  /*"Controller" when {
    val controller = new Controller( test = true )
    "new" should {
      "have game" in {
        val observer = new TestObserver()
        controller.remove( observer )
        controller.add( observer )
        controller.game.state shouldBe an[InitState]
        controller.hasUndo shouldBe false
        controller.hasRedo shouldBe false
        controller.undoAction()
        observer.error shouldBe Some( NothingToUndo )
        controller.redoAction()
        observer.error shouldBe Some( NothingToRedo )
        controller.endTurn()
        controller.update( Some( DiceInfo( 1, 2 ) ) )
        observer.updateInfo shouldBe Some( DiceInfo( 1, 2 ) )
      }
    }
    "running" should {
      "init players" in {
        controller.initPlayers()
        controller.undoAction()
        controller.redoAction()
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

  }*/
}
