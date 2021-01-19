package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID

/**
 * @author Vincent76;
 */
trait Info

case class BeginnerInfo( beginner:PlayerID, diceValues:Map[PlayerID, Int] ) extends Info

case class DiceInfo( dices:(Int, Int) ) extends Info

case class GatherInfo( dices:(Int, Int), playerResources:Map[PlayerID, ResourceCards] ) extends Info

case class GotResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info

case class LostResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info

case class ResourceChangeInfo( playerAdd:Map[PlayerID, ResourceCards], playerSub:Map[PlayerID, ResourceCards] ) extends Info

case class BuiltInfo( structure:StructurePlacement, id:Int ) extends Info

case class BankTradedInfo( pID:PlayerID, give:(Resource, Int), get:(Resource, Int) ) extends Info

case class DrawnDevCardInfo( pID:PlayerID, devCard:DevelopmentCard ) extends Info

case class InsufficientStructuresInfo( pID:PlayerID, structure:StructurePlacement ) extends Info

case class NoPlacementPointsInfo( pID:PlayerID, structure:StructurePlacement ) extends Info

case class GameEndInfo( winner:PlayerID ) extends Info
