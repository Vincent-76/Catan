package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor

/**
 * @author Vincent76;
 */

trait ControllerError extends Throwable

case object Fail extends ControllerError

case object WrongState extends ControllerError

case object InsufficientResources extends ControllerError

case object UniqueBeginnerExists extends ControllerError

case object NoUniqueBeginner extends ControllerError

case object TradePlayerInsufficientResources extends ControllerError

case class InsufficientStructures( structure:StructurePlacement ) extends ControllerError

case class NonExistentPlacementPoint( id:Int) extends ControllerError

case class PlacementPointNotEmpty( id:Int ) extends ControllerError

case object NoAdjacentStructure extends ControllerError

case class TooCloseToBuilding( id:Int ) extends ControllerError

case class NoConnectedStructures( id:Int ) extends ControllerError

case class SettlementRequired( id:Int ) extends ControllerError

case class InvalidPlacementPoint( id:Int ) extends ControllerError

case object NotEnoughPlayers extends ControllerError

case class InvalidPlayerColor( color:String ) extends ControllerError

case object RobberOnlyOnWater extends ControllerError

case class NoPlacementPoints( structure:StructurePlacement ) extends ControllerError

case class InvalidResourceAmount( amount:Int ) extends ControllerError

case class InvalidTradeResources( give:Resource, get:Resource ) extends ControllerError

case class InvalidDevCard( devCard:String ) extends ControllerError

case class InsufficientDevCards( devCard:DevelopmentCard ) extends ControllerError

case object AlreadyUsedDevCardInTurn extends ControllerError

case class DevCardDrawnInTurn( devCard:DevelopmentCard ) extends ControllerError

case object InsufficientBankResources extends ControllerError

case object InconsistentData extends ControllerError

case object DevStackIsEmpty extends ControllerError

case object PlayerNameEmpty extends ControllerError

case class PlayerNameAlreadyExists( name:String ) extends ControllerError

case class PlayerNameTooLong( name:String ) extends ControllerError

case class PlayerColorIsAlreadyInUse( playerColor:PlayerColor ) extends ControllerError

case class InvalidPlayerID( id:Int ) extends ControllerError

case class InvalidPlayer( pID:PlayerID ) extends ControllerError

case object NothingToUndo extends ControllerError

case object NothingToRedo extends ControllerError