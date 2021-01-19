package de.htwg.se.settlers.ui.tui

import de.htwg.se.settlers.model.State

/**
 * @author Vincent76;
 */
abstract class CommandAction( val command:String, val parameter:List[String] = List.empty, val desc:String ) {

  val inputPattern:String = getInputPattern

  def action( commandInput:CommandInput, state:State ):Unit

  def getSyntax:String = "[" + command + parameter.map( p => " <" + p + ">" ).mkString + "]"

  protected def getInputPattern:String = TUI.regexIgnoreCase( command )
}
