package de.htwg.se.settlers.aview.gui

import scalafx.scene.layout.Pane

/**
 * @author Vincent76;
 */
trait GUICommand {
  def getPane( gui:GUI ):Pane
}