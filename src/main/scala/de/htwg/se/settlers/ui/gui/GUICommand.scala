package de.htwg.se.settlers.ui.gui

import scalafx.scene.Node

/**
 * @author Vincent76;
 */
trait GUICommand {
  def getNode( gui:GUI ):Node
}