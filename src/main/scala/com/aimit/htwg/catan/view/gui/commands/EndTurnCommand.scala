package com.aimit.htwg.catan.view.gui.commands

import com.aimit.htwg.catan.view.gui.GUI

/**
 * @author Vincent76;
 */
case object EndTurnCommand extends SimpleGUICommand( "End Turn" ) {

  override protected def action( gui:GUI ):Unit = gui.controller.endTurn()
}
