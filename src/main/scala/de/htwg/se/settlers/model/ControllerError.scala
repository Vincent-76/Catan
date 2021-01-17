package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

//case object Success extends ControllerAnswer

sealed trait ControllerError extends Throwable

case object Fail extends ControllerError

case object WrongPhase extends ControllerError

case object InsufficientResources extends ControllerError

case object TradePlayerInsufficientResources extends ControllerError

case class InsufficientStructures( structure:StructurePlacement ) extends ControllerError

case object NonExistentPlacementPoint extends ControllerError

case object PlacementPointNotEmpty extends ControllerError

case object NoAdjacentStructure extends ControllerError

case object TooCloseToSettlement extends ControllerError

case object NoConnectedStructures extends ControllerError

case object SettlementRequired extends ControllerError

case object InvalidPlacementPoint extends ControllerError

case object NotEnoughPlayers extends ControllerError

case object InvalidPlayerColor extends ControllerError

case object RobberOnlyOnLand extends ControllerError

case object PlayerDoesntExists extends ControllerError

case object NoPlacementPoints extends ControllerError

case class WrongResourceAmount( amount:Int ) extends ControllerError

case class InvalidTradeResources( give:Resource, get:Resource ) extends ControllerError

case object InvalidDevCard extends ControllerError

case class InsufficientDevCards( devCard:DevelopmentCard ) extends ControllerError

case object AlreadyUsedDevCardInTurn extends ControllerError

case class InsufficientBankResources( r:Resource ) extends ControllerError