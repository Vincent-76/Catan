package de.htwg.se.settlers.ui.gui

import de.htwg.se.settlers.model.Game
import scalafx.geometry.Insets
import scalafx.scene.layout.{ GridPane, _ }

/**
 * @author Vincent76;
 */
class PlayerListPane extends GridPane {
  vgrow = Priority.Always
  padding = Insets( 10, 0, 0, 0 )
  columnConstraints = List( new ColumnConstraints {
    percentWidth = 100
  } )
  rowConstraints = ( 1 to Game.maxPlayers ).map( _ => new RowConstraints {
    percentHeight = 100 / Game.maxPlayers
  } )

  def update( game:Game ):Unit = {
    children = Nil
    game.players.values.zipWithIndex.foreach( p => add( new PlayerInfoPane( game, p._1 ), 0, p._2 ) )
  }

}
