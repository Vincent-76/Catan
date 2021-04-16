package de.htwg.se.settlers.model.state
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.ChangeStateCommand

/**
 * @author Vincent76;
 */
case class InitState( controller:Controller ) extends State( controller ) {

  override def initPlayers():Unit = controller.action(
    ChangeStateCommand( this, InitPlayerState( controller ) )
  )

  override def toString:String = getClass.getSimpleName
}
