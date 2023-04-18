package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object StartTurnCommand extends SimpleGUICommand( "Start Turn" ):
  override def action( gui:GUI ):Unit = gui.api.startTurn()
