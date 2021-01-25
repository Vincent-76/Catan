package de.htwg.se.settlers.ui.gui.commands

import de.htwg.se.settlers.ui.gui.GUI

/**
 * @author Vincent76;
 */
case object EndTurnCommand extends SimpleGUICommand( "End Turn" ) {

  override protected def action( gui:GUI ):Unit = gui.controller.game.state.endTurn()
}
