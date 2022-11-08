package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonValue
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import com.aimit.htwg.catan.util._
import play.api.libs.json._

import scala.util.{ Failure, Random, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */
object Card extends NamedComponent[Card] {

  type ResourceCards = Map[Resource, Int]

  object ResourceCards {
    def ofResource( r:Resource, amount:Int = 1 ):ResourceCards = Map( r -> amount )

    def of( wood:Int = 0, clay:Int = 0, sheep:Int = 0, wheat:Int = 0, ore:Int = 0 ):ResourceCards =
      Map( Wood -> wood, Clay -> clay, Sheep -> sheep, Wheat -> wheat, Ore -> ore )

    def fromXML( node:Node ):ResourceCards = node.asMap( n => Resource.of( n.content ).get, _.content.toInt )
  }

  implicit val resourceCardsReads:Reads[ResourceCards] = ( json:JsValue ) => JsSuccess( json.asMap[Resource, Int] )

  implicit class RichResourceCards( resources:ResourceCards ) {

    def add( r:Resource, amount:Int = 1 ):ResourceCards = resources.updated( r, resources.getOrElse( r, 0 ) + amount )

    def add( toAdd:ResourceCards ):ResourceCards = Resource.impls.red( resources, ( cards:ResourceCards, r:Resource ) => {
      cards.updated( r, cards.getOrElse( r, 0 ) + toAdd.getOrElse( r, 0 ) )
    } )

    def subtract( r:Resource, amount:Int = 1 ):Try[ResourceCards] = {
      val n = resources.getOrElse( r, 0 ) - amount
      if ( n >= 0 )
        Success( resources.updated( r, n ) )
      else
        Failure( InsufficientResources )
    }

    def subtract( toRemove:ResourceCards ):Try[ResourceCards] = Success( Resource.impls.red( resources, ( cards:ResourceCards, r:Resource ) => {
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

    def sort:Seq[(Resource, Int)] = resources.toList.sortBy( _._1.index )

    def toString( prefix:String ):String =
      resources.filter( _._2 > 0 ).map( r => prefix + r._2 + " " + r._1.name ).mkString( ", " )
  }

  def getResourceCards( amount:Int ):ResourceCards = {
    Resource.impls.map( r => r -> amount ).toMap
  }
}

abstract class Card( name:String ) extends NamedComponentImpl( name ) {
  override def init():Unit = Card.addImpl( this )
}




object DevelopmentCard extends NamedComponent[DevelopmentCard] {
  val cardCost:ResourceCards = Map( Sheep -> 1, Wheat -> 1, Ore -> 1 )

  KnightCard.init()
  GreatHallCard.init()
  YearOfPlentyCard.init()
  RoadBuildingCard.init()
  MonopolyCard.init()

  def getStack( random:Random = Random ):List[DevelopmentCard] = {
    random.shuffle( impls.red( List.empty, ( l:List[DevelopmentCard], d:DevelopmentCard ) => {
      l ++ ( 1 to d.amount ).map( _ => d ).toList
    } ) )
  }

  def usableOf( s:String ):Option[DevelopmentCard] =
    impls.filter( _.usable ).find( _.name ^= s )
}

abstract class DevelopmentCard( name:String, val amount:Int, val usable:Boolean, val desc:String ) extends Card( name ) {
  override def init():Unit = {
    super.init()
    DevelopmentCard.addImpl( this )
  }
}

case object KnightCard extends DevelopmentCard( "Knight",14, true,
  "Move the robber.\nSteal 1 resource from the owner of a settlement or city adjacent to the robber's new hex." )

case object GreatHallCard extends DevelopmentCard( "GreatHall", 5, false,
  "1 Victory Point!\nReveal this card on your turn if, with it, you reach the number of points required for victory." )

case object YearOfPlentyCard extends DevelopmentCard( "YearOfPlenty", 2, true,
  "Take any 2 resources from the bank. Add them to your hand. They can be 2 of the same resource or 2 different resources." )

case object RoadBuildingCard extends DevelopmentCard( "RoadBuilding", 2, true,
  "Place 2 new roads as if you had just built them." )

case object MonopolyCard extends DevelopmentCard( "Monopoly", 2, true,
  "When you play this card, announce 1 type of resource. All other players must give you all of their resources of that type." )


object BonusCard extends NamedComponent[BonusCard] {
  LargestArmyCard.init()
  LongestRoadCard.init()
}

abstract class BonusCard( name:String, val bonus:Int, val required:Int, val desc:String ) extends Card( name ) {
  override def init():Unit = {
    super.init()
    BonusCard.addImpl( this )
  }
}

case object LargestArmyCard extends BonusCard( "Largest Army", 2, 3,
  "2 Victory Points!\nThe first player to play 3 knight cards gets this card. Another player who plays more knight cards takes this card." ) {
  val minimumKnights:Int = 3
}

case object LongestRoadCard extends BonusCard(  "Longest Road",2, 2,
  "2 Victory Points!\nThis cards gets to the player with the longest road of at least 5 segments. Another player who builds a longer road takes this card." ) {
  val minimumRoads:Int = 5
}
