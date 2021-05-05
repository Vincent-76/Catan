package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.GameField.Hex
import de.htwg.se.settlers.model.{ Game, GameField }
import de.htwg.se.settlers.aview.gui.GameFieldPane.Coords
import javafx.geometry.Side
import javafx.scene.layout.{ BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize }
import scalafx.scene.image.Image
import scalafx.scene.layout.{ Background, BorderPane, StackPane }

/**
 * @author Vincent76;
 */
object GameFieldPane {
  val padding:Double = 1

  def mult( v:Double, hSize:Double ):Int = ( v * ( hSize / 45 ) ).toInt

  type Coords = Map[Hex, (Double, Double)]
}

class GameFieldPane extends BorderPane {

  val fieldPane = new GameFieldCanvas
  val overlayPane = new OverlayPane
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
    val hexes = gameField.hexagons.map( r => r.size ).max
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
    val hWidth = ( size._1 - 2 * GameFieldPane.padding ) / game.gameField.hexagons.size
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
