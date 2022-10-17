package com.aimit.htwg.catan.view.tui

import com.aimit.htwg.catan.view.tui.impl.player.ClassicPlayerDisplayImpl
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.{ Game, Player }

object PlayerDisplay {
  def get( player:Player ):PlayerDisplay = player match {
    case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )
  }
}

trait PlayerDisplay {
  def buildPlayerDisplay( game:Game ):String

  def buildTurnPlayerDisplay( game:Game ):String
}
