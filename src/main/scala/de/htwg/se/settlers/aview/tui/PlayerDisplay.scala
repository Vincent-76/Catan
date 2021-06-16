package de.htwg.se.settlers.aview.tui

import de.htwg.se.settlers.aview.tui.impl.player.ClassicPlayerDisplayImpl
import de.htwg.se.settlers.model.impl.player.ClassicPlayerImpl
import de.htwg.se.settlers.model.{ Game, Player }

object PlayerDisplay {
  def get( player:Player ):PlayerDisplay = player match {
    case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )
  }
}

trait PlayerDisplay {
  def buildPlayerDisplay( game:Game ):String

  def buildTurnPlayerDisplay( game:Game ):String
}
