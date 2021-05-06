package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object RollDicesCommand extends SimpleGUICommand( "Roll the Dices" ) {
  override def action( gui:GUI ):Unit = gui.controller.rollTheDices()
}
