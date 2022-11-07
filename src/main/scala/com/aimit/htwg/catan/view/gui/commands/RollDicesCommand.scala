package com.aimit.htwg.catan.view.gui.commands

import com.aimit.htwg.catan.view.gui.GUI

/**
 * @author Vincent76;
 */
case object RollDicesCommand extends SimpleGUICommand( "Roll the Dices" ) {
  override def action( gui:GUI ):Unit = gui.controller.action( _.rollTheDices() )
}
