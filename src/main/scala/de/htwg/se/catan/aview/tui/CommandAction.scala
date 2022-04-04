package de.htwg.se.catan.aview.tui

import de.htwg.se.catan.controller.Controller

/**
 * @author Vincent76;
 */
abstract class CommandAction( val command:String, val parameter:List[String] = List.empty, val desc:String ):

  val inputPattern:String = getInputPattern

  def action( commandInput:CommandInput, controller:Controller ):Unit

  def getSyntax:String = "[" + command + parameter.map( p => " <" + p + ">" ).mkString + "]"

  protected def getInputPattern:String = TUI.regexIgnoreCase( command )
