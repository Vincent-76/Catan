package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.model.Game
import scalafx.geometry.Insets
import scalafx.scene.layout.{ GridPane, _ }

/**
 * @author Vincent76;
 */
class PlayerListPane( gui:GUI ) extends GridPane:
  vgrow = Priority.Always
  padding = Insets( 10, 0, 0, 0 )
  columnConstraints = List( new ColumnConstraints:
    percentWidth = 100
  )
  rowConstraints = ( 1 to gui.game.maxPlayers ).map( _ => new RowConstraints:
    percentHeight = 100 / gui.game.maxPlayers
  )

  def update( game:Game ):Unit =
    children = Nil
    game.players.values.zipWithIndex.foreach( p => add( new PlayerInfoPane( game, p._1 ), 0, p._2 ) )
