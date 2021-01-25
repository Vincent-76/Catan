package de.htwg.se.settlers

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.gui.GUIApp
import de.htwg.se.settlers.ui.tui.TUI

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan {
  val controller = new Controller( true )
  val tui = new TUI( controller )
  val gui = new GUIApp( controller )

  def main( args:Array[String] ):Unit = {
    var input:String = ""
    do {
      input = StdIn.readLine()
      tui.onInput( input )
    } while( input != "exit" )
    gui.exit()
  }
}
