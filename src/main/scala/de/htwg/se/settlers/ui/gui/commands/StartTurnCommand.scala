package de.htwg.se.settlers.ui.gui.commands

import de.htwg.se.settlers.ui.gui.GUI

/**
 * @author Vincent76;
 */
case object StartTurnCommand extends SimpleGUICommand( "Start Turn" ) {
  override def action( gui:GUI ):Unit = gui.controller.game.state.startTurn()
}
