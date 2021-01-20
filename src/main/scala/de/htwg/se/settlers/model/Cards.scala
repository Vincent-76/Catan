package de.htwg.se.settlers.model

import scala.util.Random
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
object Cards {
  private val resourceCardAmount:Int = 19

  val developmentCardCost:ResourceCards = Map( Sheep -> 1, Wheat -> 1, Ore -> 1 )

  val devCards:List[DevelopmentCard] = List(
    KnightCard,
    GreatHallCard,
    YearOfPlentyCard,
    RoadBuildingCard,
    MonopolyCard
  )

  val bonusCards:List[BonusCard] = List(
    LongestRoadCard,
    LargestArmyCard
  )

  type ResourceCards = Map[Resource, Int]

  object ResourceCards {
    def ofResource( r:Resource, amount:Int = 1 ):ResourceCards = Map( r -> amount )

    def of( wood:Int = 0, clay:Int = 0, sheep:Int = 0, wheat:Int = 0, ore:Int = 0 ):ResourceCards =
      Map( Wood -> wood, Clay -> clay, Sheep -> sheep, Wheat -> wheat, Ore -> ore )
  }

  def getResourceCards( amount:Int = resourceCardAmount ):ResourceCards = {
    Resources.get.map( r => r -> amount ).toMap
  }

  def getDevStack( random:Random = Random ):List[DevelopmentCard] = {
    random.shuffle( devCards.red( List.empty, ( l:List[DevelopmentCard], d:DevelopmentCard ) => {
      l ++ ( 1 to d.amount ).map( _ => d ).toList
    } ) )
  }

  def usableDevCardOf( s:String ):Option[DevelopmentCard] =
    devCards.filter( _.usable ).find( _.t.toLowerCase == s.toLowerCase )

}

abstract class Card


abstract class DevelopmentCard( val amount:Int, val usable:Boolean, val t:String, val desc:String ) extends Card

case object KnightCard extends DevelopmentCard( 14, true, "Knight",
  "Move the robber.\nSteal 1 resource from the owner of a settlement or city adjacent to the robber's new hex." )

case object GreatHallCard extends DevelopmentCard( 5, false, "GreatHall",
  "1 Victory Point!\nReveal this card on your turn if, with it, you reach the number of points required for victory." )

case object YearOfPlentyCard extends DevelopmentCard( 2, true, "YearOfPlenty",
  "Take any 2 resources from the bank. Add them to your hand. They can be 2 of the same resource or 2 different resources." )

case object RoadBuildingCard extends DevelopmentCard( 2, true, "RoadBuilding",
  "Place 2 new roads as if you had just built them." )

case object MonopolyCard extends DevelopmentCard( 2, true, "Monopoly",
  "When you play this card, announce 1 type of resource. All other players must give you all of their resources of that type." )


abstract class BonusCard( val bonus:Int, val required:Int, val t:String, desc:String ) extends Card

case object LargestArmyCard extends BonusCard( 2, 3, "Largest Army",
  "2 Victory Points!\nThe first player to play 3 knight cards gets this card. Another player who plays more knight cards takes this card." ) {
  val minimumKnights:Int = 3
}

case object LongestRoadCard extends BonusCard( 2, 2, "Longest Road",
  "2 Victory Points!\nThis cards gets to the player with the longest road of at least 5 segments. Another player who builds a longer road takes this card." ) {
  val minimumRoads:Int = 5
}
