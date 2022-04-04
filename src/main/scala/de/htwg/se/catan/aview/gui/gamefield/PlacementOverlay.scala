package de.htwg.se.catan.aview.gui.gamefield

import de.htwg.se.catan.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.catan.aview.gui.impl.placement.{ CityPlacementOverlayImpl, RoadPlacementOverlayImpl, RobberPlacementOverlayImpl, SettlementPlacementOverlayImpl }
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.model.{ Game, Placement }
import scalafx.scene.canvas.GraphicsContext

object PlacementOverlay:
  def get( availablePlacements:List[Placement] ):List[PlacementOverlay] = availablePlacements.map {
    case RobberPlacement => RobberPlacementOverlayImpl
    case RoadPlacement => RoadPlacementOverlayImpl
    case SettlementPlacement => SettlementPlacementOverlayImpl
    case CityPlacement => CityPlacementOverlayImpl
    case c => throw new NotImplementedError( "PlacementOverlay[" + c.getClass.getName + "]" )
  }

trait PlacementOverlay:
  def draw( game:Game, context:GraphicsContext, coords:Coords, hSize:Double ):Unit
