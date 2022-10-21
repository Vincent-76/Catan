package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class ErrorTUIState( controller:Controller ) extends TUIState {

  override def createStateDisplay:Iterable[String] = List( "Error!" )

  override def getActionInfo:String = "Press Enter to undo"

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.undoAction(), Nil)
}
