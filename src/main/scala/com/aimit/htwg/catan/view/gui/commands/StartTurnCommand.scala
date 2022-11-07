package com.aimit.htwg.catan.view.gui.commands

import com.aimit.htwg.catan.view.gui.GUI

/**
 * @author Vincent76;
 */
case object StartTurnCommand extends SimpleGUICommand( "Start Turn" ) {
  override def action( gui:GUI ):Unit = gui.controller.action( _.startTurn() )
}
