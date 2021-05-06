package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.{ActionState, BuildState}
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{Success, Try}

/**
 * @author Vincent76;
 */
case class BuildCommand( id:Int, state:BuildState ) extends Command {

  var actualBonusCards:Option[Map[BonusCard, Option[(PlayerID, Int)]]] = None

  override def doStep( game:Game ):Try[(Game, Option[Info])] =
    state.structure.build( game, game.onTurn, id ) match {
      case Success( newGame ) =>
        actualBonusCards = Some( game.bonusCards )
        Success(
          newGame.setState( ActionState() ),
          Some( BuiltInfo( state.structure, id ) )
        )
      case f => f.rethrow
    }

  override def undoStep( game:Game ):Game = {
    val newGameField = state.structure match {
      case Road => game.gameField.update( game.gameField.findEdge( id ).get.setRoad( None ) )
      case Settlement => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( None ) )
      case City => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( Some( Settlement( game.onTurn ) ) ) )
      //case _ => game.gameField
    }
    game.copy(
      state = state,
      gameField = newGameField,
      bonusCards = actualBonusCards.getOrElse( game.bonusCards ),
      players = game.updatePlayers( game.player.addStructure( state.structure ) )
    )
  }

  //override def toString:String = getClass.getSimpleName + ": ID[" + id + "], " + state
}
