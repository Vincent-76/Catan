package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.aview.gui.impl.player.ClassicPlayerDisplayImpl
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.{ Game, Player }
import scalafx.scene.Node

/**
 * @author Vincent76;
 */

object PlayerDisplay {
  def get( player:Player ):PlayerDisplay = player match {
    case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )
    case c => throw new NotImplementedError( "PlayerDisplay[" + c.getClass.getName + "]" )
  }
}

trait PlayerDisplay {

  def build( gui:GUI, game:Game, full:Boolean ):List[Node]
}
