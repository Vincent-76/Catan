package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.aview.gui.impl.game.ClassicGameStackPaneImpl
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.impl.game.ClassicGameImpl
import scalafx.scene.layout._

/**
 * @author Vincent76;
 */
object GameStackPane {
  def get( game:Game ):GameStackPane[_] = game match {
    case _:ClassicGameImpl => new ClassicGameStackPaneImpl()
    case c => throw new NotImplementedError( "GameStackPane[" + c.getClass.getName + "]" )
  }
}

trait GameStackPane[T <: Game] extends VBox {

  def update( game:Game ):Unit = doUpdate( game.asInstanceOf[T] )

  protected def doUpdate( game:T ):Unit
}
