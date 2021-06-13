package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.aview.gui.impl.player.ClassicPlayerDisplayImpl
import de.htwg.se.settlers.model.impl.player.ClassicPlayerImpl
import de.htwg.se.settlers.model.{ Game, Player }
import scalafx.scene.layout.{ Priority, VBox }

class PlayerPane( gui:GUI ) extends VBox {
  background = GUIApp.woodBackground
  hgrow = Priority.Always
  style = "-fx-border-color: #353535; -fx-border-width: 0 0 2 0"
  minHeight = 220

  def update( game:Game, playerData:Option[(Player, Boolean)] ):Unit = children = playerData match {
    case None => Nil
    case Some( (player, full) ) => ( player match {
      case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )

    } ).build( gui, game, full )
  }
}
