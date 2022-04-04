package de.htwg.se.catan.aview.tui

import de.htwg.se.catan.aview.tui.impl.player.ClassicPlayerDisplayImpl
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.{ Game, Player }

object PlayerDisplay:
  def get( player:Player ):PlayerDisplay = player match
    case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )

trait PlayerDisplay:
  def buildPlayerDisplay( game:Game ):String

  def buildTurnPlayerDisplay( game:Game ):String
