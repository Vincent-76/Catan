package de.htwg.se.settlers.ui

import java.io.ByteArrayInputStream

import de.htwg.se.settlers.controller.Controller
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author Vincent76;
 */
/*class TUISpec extends AnyWordSpec with Matchers {
  "A TUI" when {
    "new" should {
      val controller = new Controller()
      val tui = new TUI( controller )
      "run" in {
        val in = new ByteArrayInputStream( "\n".getBytes() )
        Console.withIn( in )( tui.listen() ) should be ( true )
      }
      "transform parameter" in {
        tui.getParameter( "test 1 2" ) should be( Vector( "1", "2" ) )
      }
      "confirm" in {
        Console.withIn( new ByteArrayInputStream( "Y".getBytes() ) )( tui.confirmed ) should be( true )
      }
      "find global command" in {
        tui.findGlobalCommand( CommandInput( "test" ) ) should be( Option.empty )
      }
      "transform regex" in {
        tui.regexIgnoreCase( "TeSt" ) should be( "[Tt][Ee][Ss][Tt]" )
      }
      "init" in {
        tui.initAction( CommandInput( "" ) ) should be( Option.empty )
      }
    }
  }
}*/
