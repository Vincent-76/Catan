package com.aimit.htwg.catan.view.tui

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */
abstract class CommandAction( val command:String, val parameter:List[String] = List.empty, val desc:String ) {

  val inputPattern:String = getInputPattern

  def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String])

  def getSyntax:String = "[" + command + parameter.map( p => " <" + p + ">" ).mkString + "]"

  protected def getInputPattern:String = TUI.regexIgnoreCase( command )
}
