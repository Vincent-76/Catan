package com.aimit.htwg.catan.controller

import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.placement.RoadPlacement
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.state._
import com.aimit.htwg.catan.model.{ KnightCard, _ }
import com.aimit.htwg.catan.util._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File

/**
 * @author Vincent76;
 */
class ControllerSpec extends AnyWordSpec with Matchers {
  class TestObserver extends Observer {
    var updateInfo:Option[Info] = None
    var info:Option[Info] = None
    var error:Option[Throwable] = None

    override def onUpdate(info: Option[Info]): Unit = this.updateInfo = info

    override def onInfo(info: Info): Unit = this.info = Some( info )

    override def onError(t: Throwable): Unit = this.error = Some( t )
  }

  CatanModule.init()

  "Controller" when {
    val controller = Controller( new ClassicGameImpl( ClassicGameFieldImpl( 1 ), ClassicTurnImpl(), 1, ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ), "ClassicPlayerImpl" ), XMLFileIO )
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
        controller.action( _.endTurn() )
        controller.update( Some( DiceInfo( 1, 2 ) ) )
        observer.updateInfo shouldBe Some( DiceInfo( 1, 2 ) )
      }
      "save and load game" in {
        val game = controller.game.asInstanceOf[ClassicGameImpl].copy( playerFactory = null )
        val savedResult = controller.saveGame()
        savedResult.isSuccess shouldBe true
        val savedInfo = savedResult.get
        savedInfo.isDefined shouldBe true
        savedInfo.get shouldBe a [GameSavedInfo]
        val path = savedInfo.get.asInstanceOf[GameSavedInfo].path
        controller.loadGame( path )
        val file = new File( path )
        if( file.exists )
          file.delete()
        controller.game.asInstanceOf[ClassicGameImpl].copy( playerFactory = null ) shouldBe game
      }
    }
    "running" should {
      "init players" in {
        controller.action( _.initGame() )
        controller.undoAction()
        controller.redoAction()
        controller.action( _.addPlayer( Green, "A" ) )
        controller.action( _.addPlayer( Blue, "A" ) )
        controller.action( _.addPlayer( Blue, "B" ) )
        controller.action( _.addPlayer( Yellow, "C" ) )
        controller.action( _.setInitBeginnerState() )
        controller.action( _.diceOutBeginner() )
        controller.action( _.diceOutBeginner() )
        controller.action( _.setBeginner() )
        controller.onTurn.id shouldBe 1
        controller.player.id.id shouldBe 1
        controller.player( controller.onTurn ).id.id shouldBe 1
        val oldGame = controller.game
        val p = ( 0 until controller.game.requiredVictoryPoints - controller.player.victoryPoints ).red( controller.player,
          ( p:Player, _ ) => p.addVictoryPoint()
        )
        controller.game = controller.game.updatePlayer( p )
        controller.action( _.buildInitSettlement( 21 ) )
        controller.game.winner shouldBe Some( p.id )
        controller.running shouldBe false
        controller.undoAction()
        controller.game = oldGame
        ( 1 to 8 ).foreach( _ => controller.undoAction() )
      }
      "call state methods" in {
        controller.action( _.addPlayer( Green, "A" ) )
        controller.action( _.setInitBeginnerState() )
        controller.action( _.initGame() )
        controller.action( _.initGame() )
        controller.action( _.diceOutBeginner() )
        controller.action( _.setBeginner() )
        controller.action( _.buildInitSettlement( 0 ) )
        controller.action( _.buildInitRoad( 0 ) )
        controller.action( _.startTurn() )
        controller.action( _.rollTheDices() )
        controller.action( _.useDevCard( KnightCard ) )
        controller.action( _.dropResourceCardsToRobber( ResourceCards.of() ) )
        controller.action( _.placeRobber( 0 ) )
        controller.action( _.robberStealFromPlayer( controller.onTurn ) )
        controller.action( _.setBuildState( RoadPlacement ) )
        controller.action( _.build( 0 ) )
        controller.action( _.bankTrade( ResourceCards.of(), ResourceCards.of() ) )
        controller.action( _.setPlayerTradeState( ResourceCards.of(), ResourceCards.of() ) )
        controller.action( _.playerTradeDecision( false ) )
        controller.action( _.abortPlayerTrade() )
        controller.action( _.playerTrade( controller.onTurn ) )
        controller.action( _.buyDevCard() )
        controller.action( _.yearOfPlentyAction( ResourceCards.of() ) )
        controller.action( _.devBuildRoad( 0 ) )
        controller.action( _.monopolyAction( Wood ) )
        controller.action( _.endTurn() )
      }
    }

  }
}
