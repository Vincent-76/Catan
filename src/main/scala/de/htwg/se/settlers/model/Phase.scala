package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.GameField.Vertex

/**
 * @author Vincent76;
 */
sealed trait Phase

case object InitPhase extends Phase

case object InitPlayerPhase extends Phase

case object InitBeginnerPhase extends Phase

case object InitBuildSettlementPhase extends Phase

case class InitBuildRoadPhase( settlementVID:Int ) extends Phase

case object NextPlayerPhase extends Phase

case object TurnStartPhase extends Phase

case object DicePhase extends Phase

case class DropResourceCardPhase( dropped:List[Int] ) extends Phase

case class GatherPhase( dices:(Int, Int), playerResources:Map[Int, ResourceCards] ) extends Phase

case class RobberPlacePhase( nextPhase:Phase ) extends Phase

case class RobberStealPhase( nextPhase:Phase ) extends Phase

case object ActionPhase extends Phase

case class BuildPhase( structure:StructurePlacement ) extends Phase

case class PlayerTradePhase( give:ResourceCards, get:ResourceCards, decisions:Map[Int, Boolean] ) extends Phase

case class DevYearOfPlentyPhase( nextPhase:Phase ) extends Phase

case class DevRoadBuildingPhase( nextPhase:Phase, roads:Int = 0 ) extends Phase

case class DevMonopolyPhase( nextPhase:Phase ) extends Phase
