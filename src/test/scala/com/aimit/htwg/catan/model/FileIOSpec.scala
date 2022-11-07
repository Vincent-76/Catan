package com.aimit.htwg.catan.model

import com.google.inject.{ Guice, Injector }
import com.aimit.htwg.catan.{ CatanModule, ClassicCatanModule }
import com.aimit.htwg.catan.model.commands.InitGameCommand
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File


class FileIOSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( ClassicCatanModule.create( test = true ) )
  "FileIO" when {
    "created" should {
      "load" in {
        val game = new ClassicGameImpl(
          injector.getInstance( classOf[GameField] ),
          injector.getInstance( classOf[Turn] ), 1,
          injector.getInstance( classOf[PlayerFactory] ),
          "ClassicPlayerImpl"
        )
        val undoStack = List( InitGameCommand() )
        val redoStack = List( InitGameCommand() )
        val path = JsonFileIO.save( game, undoStack, redoStack )
        val (game2, undoStack2, redoStack2) = FileIO.load( path )._2
        val file = new File( path )
        if( file.exists )
          file.delete()
        game2.asInstanceOf[ClassicGameImpl].copy( playerFactory = null ) shouldBe game.copy( playerFactory = null )
        undoStack2 shouldBe undoStack
        redoStack2 shouldBe redoStack
        intercept[NotImplementedError] {
          FileIO.load( path.replace( "json", "abc" ) )
        }
      }
    }
  }
}
