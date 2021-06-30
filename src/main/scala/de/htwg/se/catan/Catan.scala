package de.htwg.se.catan

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.aview.gui.GUIApp
import de.htwg.se.catan.aview.tui.TUI
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.{ DesertArea, DevelopmentCard, Game, GreatHallCard, Green, KnightCard, PlayerColor, PlayerID }
import de.htwg.se.catan.model.impl.game
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state.{ InitBeginnerState, InitState }
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
    args.foreach( s => println( s ) )
    var input:String = ""
    do {
      input = StdIn.readLine()
      tui.onInput( input )
    } while( input != "exit" )
    if( gui.isDefined )
      gui.get.exit()
  }
}
