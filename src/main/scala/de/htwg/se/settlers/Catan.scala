package de.htwg.se.settlers

import de.htwg.se.settlers.aview.gui.GUIApp
import de.htwg.se.settlers.aview.tui.TUI
import de.htwg.se.settlers.controller.controllerBaseImpl.ClassicControllerImpl

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan {
  val debug = false
  val controller = new ClassicControllerImpl( test = false/*, debug = debug*/ )
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
