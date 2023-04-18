package de.htwg.se.catan.aview.gui

import scalafx.scene.Node

/**
 * @author Vincent76;
 */
trait GUICommand:
  def getNode( gui:GUI ):Node