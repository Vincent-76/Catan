package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Cards._
import de.htwg.se.catan.model.state.YearOfPlentyState
import de.htwg.se.catan.model._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class YearOfPlentyCommand( resources:ResourceCards, state:YearOfPlentyState ) extends Command {

  var drawnResources:Option[ResourceCards] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( resources.amount != 2 )
      Failure( InvalidResourceAmount( resources.amount ) )
    else {
      val (availableResources, _) = game.getAvailableResourceCards( resources )
      drawnResources = Some( availableResources )
      success(
        game.setState( state.nextState ).drawResourceCards( game.onTurn, availableResources )._1,
        info = Some( GotResourcesInfo( game.onTurn, availableResources ) )
      )
    }
  }

  override def undoStep( game:Game ):Game = (drawnResources match {
    case None => game
    case Some( available ) => game.dropResourceCards( game.onTurn, available ).get
  }).setState( state )

  //override def toString:String = getClass.getSimpleName + ": resources[" + resources + "], " + state
}
