package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.CommandInput

/**
 * @author Vincent76;
 */
abstract class PhaseAction( controller:Controller ) {
  val gameDisplay:Option[String] = getGameDisplay

  def getGameDisplay:Option[String] = Option.empty

  def actionInfo:Option[String]

  def inputPattern:Option[String] = Option.empty

  def action( commandInput:CommandInput ):Option[Throwable] = Option.empty

}
