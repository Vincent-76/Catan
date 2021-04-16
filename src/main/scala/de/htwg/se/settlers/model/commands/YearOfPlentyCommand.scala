package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.state.YearOfPlentyState
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class YearOfPlentyCommand( resources:ResourceCards, state:YearOfPlentyState ) extends Command {

  var drawnResources:Option[ResourceCards] = Option.empty

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( resources.amount != 2 )
      Failure( InvalidResourceAmount( resources.amount ) )
    else {
      val (availableResources, _) = game.getAvailableResourceCards( resources )
      drawnResources = Some( availableResources )
      Success(
        game.drawResourceCards( game.onTurn, availableResources ).setState( state.nextState ),
        Some( GotResourcesInfo( game.onTurn, availableResources ) )
      )
    }
  }

  override def undoStep( game:Game ):Game = ( drawnResources match {
    case None => game
    case Some( available ) => game.dropResourceCards( game.onTurn, available ).get
  } ).setState( state )

  override def toString:String = getClass.getSimpleName + ": resources[" + resources + "], " + state
}
