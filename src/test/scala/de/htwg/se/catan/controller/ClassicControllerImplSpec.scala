package de.htwg.se.catan.controller

import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.controller.controllerBaseImpl.ClassicControllerImpl
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.error.{ NothingToUndo, NothingToRedo }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.RoadPlacement
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.info.DiceInfo
import de.htwg.se.catan.model.state.*
import de.htwg.se.catan.model.{ KnightCard, * }
import de.htwg.se.catan.util.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File

/**
 * @author Vincent76;
 */
class ClassicControllerImplSpec extends AnyWordSpec with Matchers {
  class TestObserver extends Observer {
    var updateInfo:Option[Info] = None
    var info:Option[Info] = None
    var error:Option[Throwable] = None

    override def onUpdate( game:Game, info: Option[Info]): Unit = this.updateInfo = info

    override def onInfo(info: Info): Unit = this.info = Some( info )

    override def onError(t: Throwable): Unit = this.error = Some( t )
  }

  CatanModule.init()

  "ClassicControllerImpl" when {
    val controller = new ClassicControllerImpl( new ClassicGameImpl( ClassicGameFieldImpl( 1 ), ClassicTurnImpl(), 1, ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ), "ClassicPlayerImpl" ), XMLFileIO )
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
        controller.update( controller.game, Some( DiceInfo( 1, 2 ) ) )
        observer.updateInfo shouldBe Some( DiceInfo( 1, 2 ) )
      }
      "save and load game" in {
        val game = controller.gameVal.asInstanceOf[ClassicGameImpl].copy( playerFactory = null )
        val path = controller.saveGame()
        controller.loadGame( path )
        val file = new File( path )
        if( file.exists )
          file.delete()
        controller.gameVal.asInstanceOf[ClassicGameImpl].copy( playerFactory = null ) shouldBe game
      }
    }
    "running" should {
      "init players" in {
        controller.initGame()
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
        val p = ( 0 until controller.game.requiredVictoryPoints - controller.player.victoryPoints ).red( controller.player,
          ( p:Player, _ ) => p.addVictoryPoint()
        )
        controller.gameVal = controller.game.updatePlayer( p )
        controller.buildInitSettlement( 21 )
        controller.game.winner shouldBe Some( p.id )
        controller.running shouldBe false
        controller.undoAction()
        controller.gameVal = oldGame
        ( 1 to 8 ).foreach( _ => controller.undoAction() )
      }
      "call state methods" in {
        controller.addPlayer( Green, "A" )
        controller.setInitBeginnerState()
        controller.initGame()
        controller.initGame()
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
        controller.setBuildState( RoadPlacement )
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

  }
}
