package com.aimit.htwg.catan.view.gui

import scalafx.scene.Node

/**
 * @author Vincent76;
 */
trait GUICommand {
  def getNode( gui:GUI ):Node
}