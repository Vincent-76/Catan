package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object StartTurnCommand extends SimpleGUICommand( "Start Turn" ) {
  override def action( gui:GUI ):Unit = gui.controller.startTurn()
}
