package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.Game
import scalafx.scene.Node

/**
 * @author Vincent76;
 */
trait PlayerDisplay {

  def build( gui:GUI, game:Game, full:Boolean ):List[Node]
}
