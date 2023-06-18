package de.htwg.se.catan

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
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
import de.htwg.se.catan.web.Requests
import javafx.application.Application
import play.api.libs.json.{ JsSuccess, Json }

import scala.io.StdIn

/**
 * @author Vincent76;
 */
object Catan:
  val debug = false
  CatanModule.init()
  val injector:Injector = Guice.createInjector( CatanModule( test = true ) )
  val controller:Controller = injector.getInstance( classOf[Controller] )
  val tui:TUI = TUI( controller )
  val requests:Requests = Requests( controller )

  def main( args:Array[String] ):Unit =
    /*val gui:Option[GUIApp] = if args.contains( "gui" ) then
      Some( GUIApp( controller ) )
    else None*/
    var input:String = ""
    input = StdIn.readLine()
    if input != null then tui.onInput( input )
    while input != "exit" do
      input = StdIn.readLine()
      if input != null then tui.onInput( input )
    /*if gui.isDefined then
      gui.get.exit()*/