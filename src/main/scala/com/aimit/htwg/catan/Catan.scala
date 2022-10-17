package com.aimit.htwg.catan

import com.google.inject.{ Guice, Injector }
import com.aimit.htwg.catan.view.gui.GUIApp
import com.aimit.htwg.catan.view.tui.TUI
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ DesertArea, DevelopmentCard, Game, GreatHallCard, Green, KnightCard, PlayerColor, PlayerID }
import com.aimit.htwg.catan.model.impl.game
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.state.{ InitBeginnerState, InitState }
import play.api.libs.json.{ JsSuccess, Json }

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan {
  val debug = false
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = false ) )
  val controller:Controller = injector.getInstance( classOf[Controller] )
  val tui:TUI = new TUI( controller )

  def main( args:Array[String] ):Unit = {
    val gui:Option[GUIApp] = if( args.contains( "gui" ) )
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
