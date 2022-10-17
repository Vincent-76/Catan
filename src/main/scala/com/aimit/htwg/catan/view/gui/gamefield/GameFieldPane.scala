package com.aimit.htwg.catan.view.gui.gamefield

import com.aimit.htwg.catan.view.gui.gamefield.GameFieldPane.Coords
import com.aimit.htwg.catan.model.{ Game, GameField, Hex }
import scalafx.scene.layout.{ BorderPane, StackPane }

/**
 * @author Vincent76;
 */
object GameFieldPane {
  val padding:Double = 1

  def mult( v:Double, hSize:Double ):Int = ( v * ( hSize / 45 ) ).toInt

  type Coords = Map[Hex, (Double, Double)]
}

class GameFieldPane( val fieldPane:GameFieldCanvas[_], placements:List[PlacementOverlay] ) extends BorderPane {

  val overlayPane = new OverlayPane( placements )
  val interactionPane:InteractionPane = new InteractionPane

  var coords:Coords = Map.empty
  var hSize:Double = 0

  object GameFieldContainer extends StackPane {
    children = List(
      fieldPane,
      overlayPane,
      interactionPane
    )
  }

  private def getChildSize( gameField:GameField ):(Double, Double) = {
    val hexes = gameField.fieldWidth
    val ratio = ( ( 1 / ( hexes * Math.sqrt( 3 ) ) ) * ( ( 2d / 4 ) + ( hexes * ( 6d / 4 ) ) ) ) / 1
    if ( width.value < height.value / ratio )
      (width.value - 20, ( width.value - 20 ) * ratio)
    else
      (( height.value - 20 ) / ratio, height.value - 20)
  }

  def updateAll( game:Game ):Unit = {
    val size = getChildSize( game.gameField )
    center = GameFieldContainer
    GameFieldContainer.minWidth = size._1
    GameFieldContainer.minHeight = size._2
    GameFieldContainer.maxWidth = size._1
    GameFieldContainer.maxHeight = size._2
    fieldPane.width = size._1
    fieldPane.height = size._2
    val hWidth = ( size._1 - 2 * GameFieldPane.padding ) / game.gameField.fieldHeight
    hSize = hWidth / Math.sqrt( 3 )
    coords = fieldPane.update( game.gameField, hWidth, hSize )
    overlayPane.width = size._1
    overlayPane.height = size._2
    updateOverlay( game )
  }

  def updateOverlay( game:Game ):Unit = {
    overlayPane.update( game, coords, hSize )
    interactionPane.update( coords, hSize )
  }
}
