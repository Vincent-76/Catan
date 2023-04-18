package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.model.{ Game, Player }
import scalafx.scene.layout.{ Priority, VBox }

class PlayerPane( gui:GUI ) extends VBox:
  background = GUI.woodBackground
  hgrow = Priority.Always
  style = "-fx-border-color: #353535; -fx-border-width: 0 0 2 0"
  minHeight = 220

  def update( game:Game, playerData:Option[(Player, Boolean)] ):Unit = children = playerData match
    case None => Nil
    case Some( (player, full) ) => PlayerDisplay.get( player ).build( gui, game, full )
