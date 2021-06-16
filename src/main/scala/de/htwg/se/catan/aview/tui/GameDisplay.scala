package de.htwg.se.catan.aview.tui

import de.htwg.se.catan.aview.tui.impl.game.ClassicGameDisplayImpl
import de.htwg.se.catan.model.Game
import de.htwg.se.catan.model.impl.game.ClassicGameImpl

object GameDisplay {
  def get( game:Game ):GameDisplay[_] = game match {
    case _:ClassicGameImpl => ClassicGameDisplayImpl
    case c => throw new NotImplementedError( "GameDisplay[" + c.getClass.getName + "]" )
  }
}

trait GameDisplay[T <: Game] {
  def buildGameLegend( game:Game ):Vector[(String, String)] =
    doBuildGameLegend( game.asInstanceOf[T] )

  protected def doBuildGameLegend( game:T ):Vector[(String, String)]
}
