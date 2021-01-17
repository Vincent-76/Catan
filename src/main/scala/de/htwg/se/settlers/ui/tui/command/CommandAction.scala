package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
abstract class CommandAction( val command:String, val parameter:List[String] = List.empty, val desc:String ) {

  val inputPattern:String = getInputPattern

  def action( commandInput:CommandInput, controller:Controller ):Option[Throwable]

  def getSyntax:String = "[" + command + parameter.map( p => " <" + p + ">" ).mkString + "]"

  protected def getInputPattern:String = TUI.regexIgnoreCase( command )
}
