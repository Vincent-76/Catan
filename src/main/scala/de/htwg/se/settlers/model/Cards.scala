package de.htwg.se.settlers.model

import de.htwg.se.settlers.util._

import scala.util.{Failure, Random, Success, Try}

/**
 * @author Vincent76;
 */
object Cards {

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

  implicit class RichResourceCards( resources:ResourceCards ) {

    def add( r:Resource, amount:Int = 1 ):ResourceCards = resources.updated( r, resources.getOrElse( r, 0 ) + amount )

    def add( toAdd:ResourceCards ):ResourceCards = Resources.get.red( resources, ( cards:ResourceCards, r:Resource ) => {
      cards.updated( r, cards.getOrElse( r, 0 ) + toAdd.getOrElse( r, 0 ) )
    } )

    def subtract( r:Resource, amount:Int = 1 ):Try[ResourceCards] = {
      val n = resources.getOrElse( r, 0 ) - amount
      if ( n >= 0 )
        Success( resources.updated( r, n ) )
      else
        Failure( InsufficientResources )
    }

    def subtract( toRemove:ResourceCards ):Try[ResourceCards] = Success( Resources.get.red( resources, ( cards:ResourceCards, r:Resource ) => {
      val n = cards.getOrElse( r, 0 ) - toRemove.getOrElse( r, 0 )
      if ( n < 0 )
        return Failure( InsufficientResources )
      cards.updated( r, n )
    } ) )

    def amount:Int = resources.values.sum

    def has( requiredResources:ResourceCards ):Boolean = {
      requiredResources.foreach( data => if ( !resources.contains( data._1 ) || resources( data._1 ) < data._2 ) return false )
      true
    }

    def sort:Seq[(Resource, Int)] = resources.sortBySeq( Resources.get )

    def toString( prefix:String ):String =
      resources.filter( _._2 > 0 ).map( r => prefix + r._2 + " " + r._1.title ).mkString( ", " )
  }

  def getResourceCards( amount:Int ):ResourceCards = {
    Resources.get.map( r => r -> amount ).toMap
  }

  def getDevStack( random:Random = Random ):List[DevelopmentCard] = {
    random.shuffle( devCards.red( List.empty, ( l:List[DevelopmentCard], d:DevelopmentCard ) => {
      l ++ ( 1 to d.amount ).map( _ => d ).toList
    } ) )
  }

  def usableDevCardOf( s:String ):Option[DevelopmentCard] =
    devCards.filter( _.usable ).find( _.title.toLowerCase == s.toLowerCase )

}

abstract class Card


abstract class DevelopmentCard( val amount:Int, val usable:Boolean, val title:String, val desc:String ) extends Card {
  override def toString:String = title
}

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


abstract class BonusCard( val bonus:Int, val required:Int, val title:String, desc:String ) extends Card

case object LargestArmyCard extends BonusCard( 2, 3, "Largest Army",
  "2 Victory Points!\nThe first player to play 3 knight cards gets this card. Another player who plays more knight cards takes this card." ) {
  val minimumKnights:Int = 3
}

case object LongestRoadCard extends BonusCard( 2, 2, "Longest Road",
  "2 Victory Points!\nThis cards gets to the player with the longest road of at least 5 segments. Another player who builds a longer road takes this card." ) {
  val minimumRoads:Int = 5
}
