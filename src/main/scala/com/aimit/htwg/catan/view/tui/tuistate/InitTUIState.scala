package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class InitTUIState( controller:Controller ) extends TUIState {

  override def createStateDisplay:Iterable[String] = List(
    "Welcome to Settlers of Catan.",
    "Type [help] for a list of all available commands."
  )

  override def getActionInfo:String = "Press Enter to add players"

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.initGame(), Nil)

}
