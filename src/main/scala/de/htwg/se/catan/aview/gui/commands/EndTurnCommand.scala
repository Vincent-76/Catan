package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.aview.gui.GUI

/**
 * @author Vincent76;
 */
case object EndTurnCommand extends SimpleGUICommand( "End Turn" ):

  override protected def action( gui:GUI ):Unit = gui.api.endTurn()
