package de.htwg.se.catan.model

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.impl.fileio.JsonFileIO
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import org.scalatest.{ Matchers, WordSpec }

class FileIOSpec extends WordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  "FileIO" when {
    "created" should {
      "load" in {
        val game = new ClassicGameImpl(
          injector.getInstance( classOf[GameField] ),
          injector.getInstance( classOf[Turn] ), 1,
          injector.getInstance( classOf[PlayerFactory] ),
          "ClassicPlayerImpl"
        )
        val path = JsonFileIO.save( game )
        FileIO.load( path ).asInstanceOf[ClassicGameImpl].copy( playerFactory = null ) shouldBe game.copy( playerFactory = null )
        intercept[NotImplementedError] {
          FileIO.load( path.replace( "json", "abc" ) )
        }
      }
    }
  }
}
