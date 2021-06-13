package de.htwg.se.settlers.aview.gui.gamefield

import de.htwg.se.settlers.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.settlers.model.Game
import scalafx.scene.canvas.GraphicsContext

trait PlacementOverlay {
  def draw( game:Game, context:GraphicsContext, coords:Coords, hSize:Double ):Unit
}
