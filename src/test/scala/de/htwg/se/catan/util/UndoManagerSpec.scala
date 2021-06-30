package de.htwg.se.catan.util

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.commands.{ AddPlayerCommand, InitGameCommand }
import de.htwg.se.catan.model.state.InitPlayerState
import de.htwg.se.catan.model.{ Game, Green, NothingToRedo, NothingToUndo, PlayerNameEmpty }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{ Failure, Success }

class UndoManagerSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  val game = injector.getInstance( classOf[Game] )
  "UndoManager" when {
    val undoManager = UndoManager()
    "new" should {
      "not hasUndo" in {
        undoManager.hasUndo shouldBe false
      }
      "not hasRedo" in {
        undoManager.hasRedo shouldBe false
      }
      "fail undoStep" in {
        undoManager.undoStep( game ) shouldBe Failure( NothingToUndo )
      }
      "fail redoStep" in {
        undoManager.redoStep( game ) shouldBe Failure( NothingToRedo )
      }
    }
    "running" should {
      "clear" in {
        undoManager.clear()
        undoManager.hasUndo shouldBe false
        undoManager.hasRedo shouldBe false
      }
      "fail doStep" in {
        undoManager.doStep( AddPlayerCommand( Green, "", InitPlayerState() ), game ) shouldBe
          Failure( PlayerNameEmpty )
        undoManager.clear()
      }
      "fail redoStep" in {
        undoManager.redoStack = AddPlayerCommand( Green, "", InitPlayerState() ) :: undoManager.redoStack
        val res = undoManager.redoStep( game )
        res shouldBe Failure( PlayerNameEmpty )
        undoManager.clear()
      }
      "doStep" in {
        undoManager.doStep( InitGameCommand(), game ) shouldBe a [Success[_]]
        undoManager.clear()
      }
      "undoStep" in {
        val res = undoManager.doStep( InitGameCommand(), game )
        res shouldNot be( None )
        undoManager.undoStep( res.get._1 ) shouldBe Success( game )
        undoManager.clear()
      }
      "redoStep" in {
        val res = undoManager.doStep( InitGameCommand(), game )
        res shouldNot be( None )
        val res2 = undoManager.undoStep( res.get._1 )
        res2 shouldNot be( None )
        val res3 = undoManager.redoStep( res2.get )
        res3 shouldBe a [Success[_]]
        res3.get._1 shouldBe res.get._1
        undoManager.clear()
      }
    }
  }
}
