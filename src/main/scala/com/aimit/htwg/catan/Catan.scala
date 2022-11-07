package com.aimit.htwg.catan

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Game
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO
import com.aimit.htwg.catan.view.gui.GUIApp
import com.aimit.htwg.catan.view.tui.TUI
import com.google.inject.{ Guice, Injector }

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan {
  val debug = false
  CatanModule.init()
  val injector:Injector = Guice.createInjector( ClassicCatanModule( test = false ) )
  val controller:Controller = new Controller( injector.getInstance( classOf[Game] ), JsonFileIO )
  val tui:TUI = new TUI( controller )

  def main( args:Array[String] ):Unit = {
    val gui:Option[GUIApp] = if( !args.contains( "tui" ) )
      Some( new GUIApp( controller ) )
    else None
    var input:String = ""
    do {
      input = StdIn.readLine()
      tui.onInput( input )
    } while( input != "exit" )
    if( gui.isDefined )
      gui.get.exit()
  }
}
