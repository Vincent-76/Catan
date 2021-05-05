package de.htwg.se.settlers.aview.tui

/**
 * @author Vincent76;
 */
trait TUIState {
  def getGameDisplay:Option[String] = Option.empty

  def getActionInfo:String

  def inputPattern:Option[String] = Option.empty

  def action( commandInput: CommandInput ):Unit
}
