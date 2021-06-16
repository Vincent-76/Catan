package de.htwg.se.settlers

import com.google.inject.{ Guice, Injector }
import de.htwg.se.settlers.aview.gui.GUIApp
import de.htwg.se.settlers.aview.tui.TUI
import de.htwg.se.settlers.controller.Controller

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan {
  val debug = false
  val injector:Injector = Guice.createInjector( new CatanModule )
  val controller:Controller = injector.getInstance( classOf[Controller] )
  val tui:TUI = new TUI( controller )
  val gui:GUIApp = new GUIApp( controller )

  def main( args:Array[String] ):Unit = {
    var input:String = ""
    do {
      input = StdIn.readLine()
      tui.onInput( input )
    } while( input != "exit" )
    gui.exit()
  }
}
