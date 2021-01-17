package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
object Player {
  val colors:Vector[PlayerColor] = Vector(
    Green,
    Red,
    Magenta,
    Cyan
  )

  def colorOf( cString:String ):Option[PlayerColor] = {
    colors.find( _.name.toLowerCase == cString.toLowerCase() ).use( c => if ( c.isDefined ) Some( c.get ) else Option.empty )
  }

  def availableColors( players:Vector[Player] = Vector.empty ):Vector[PlayerColor] = {
    players.red( colors, ( c:Vector[PlayerColor], p:Player ) => {
      val index = c.indexWhere( _ == p.color )
      if ( index >= 0 )
        c.removeAt( index )
      else
        c
    } )
  }
}

case class Player( id:Int,
                   color:PlayerColor,
                   name:String,
                   resources:ResourceCards = Cards.getResourceCards( 0 ),
                   devCards:Vector[DevelopmentCard] = Vector.empty,
                   usedDevCards:Vector[DevelopmentCard] = Vector.empty,
                   victoryPoints:Int = 0,
                   structures:Map[StructurePlacement, Int] = StructurePlacement.get.map( p => (p, p.available) ).toMap
                 ) {

  def idName:String = "<" + id + ">" + name

  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[Player] = resources.subtract( resource, amount ) match {
    case Success( newResources ) => Success( copy( resources = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def removeResourceCards( cards:ResourceCards ):Try[Player] = resources.subtract( cards ) match {
    case Success( newResources ) => Success( copy( resources = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def addResourceCard( resource:Resource, amount:Int = 1 ):Player = copy( resources = resources.add( resource ) )

  def addResourceCards( cards:ResourceCards ):Player = copy( resources = resources.add( cards ) )

  def trade( get:ResourceCards, give:ResourceCards ):Try[Player] = addResourceCards( get ).removeResourceCards( give )

  def addDevCard( card:DevelopmentCard ):Player = copy( devCards = devCards :+ card )

  def addVictoryPoint( ):Player = copy( victoryPoints = victoryPoints + 1 )

  def getVictoryPoints( game:Game ):Int = {
    getDisplayVictoryPoints( game ) + devCards.count( _ == GreatHallCard )
  }

  def getDisplayVictoryPoints( game:Game ):Int =
    getBonusCards( game ).red( victoryPoints, ( points:Int, bonusCard:BonusCard ) => points + bonusCard.bonus )

  def getBonusCards( game:Game ):Iterable[BonusCard] =
    game.bonusCards.filter( d => d._2.isDefined && d._2.get._1 == id ).keys

  def hasStructure( structure:StructurePlacement ):Boolean = structures.getOrElse( structure, 0 ) > 0

  def getStructure( structure:StructurePlacement ):Try[Player] = {
    val available = structures.getOrElse( structure, 0 )
    if ( available > 0 )
      Success( copy( structures = structures.updated( structure, available - 1 ) ) )
    else
      Failure( InsufficientStructures( structure ) )
  }

  def randomHandResource( ):Option[Resource] = {
    Random.element( resources.red( List.empty, ( l:List[Resource], r:Resource, amount:Int ) => {
      ( 0 until amount ).red( l, ( l:List[Resource], _ ) => {
        l :+ r
      } )
    } ) )
  }

  def useDevCard( devCard:DevelopmentCard ):Try[Player] = {
    val index = devCards.indexOf( devCard )
    if ( index >= 0 )
      Success( copy(
        devCards = devCards.removeAt( index ),
        usedDevCards = usedDevCards :+ devCard
      ) )
    else Failure( InsufficientDevCards( devCard ) )
  }
}
