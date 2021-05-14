package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
object Player {

  sealed abstract class PlayerColor( val name:String )


  case object Green extends PlayerColor( "Green" )

  case object Blue extends PlayerColor( "Blue" )

  case object Yellow extends PlayerColor( "Yellow" )

  case object Red extends PlayerColor( "Red" )

  val colors:List[PlayerColor] = List(
    Green,
    Blue,
    Yellow,
    Red
  )

  def colorOf( cString:String ):Option[PlayerColor] = colors.find( _.name.toLowerCase == cString.toLowerCase() )

  def availableColors( players:Iterable[Player] = Vector.empty ):Seq[PlayerColor] = {
    players.red( colors, ( c:Seq[PlayerColor], p:Player ) => {
      c.removed( p.color )
    } )
  }
}

case class Player( id:PlayerID,
                   color:PlayerColor,
                   name:String,
                   resources:ResourceCards = Cards.getResourceCards( 0 ),
                   devCards:Vector[DevelopmentCard] = Vector.empty,
                   usedDevCards:Vector[DevelopmentCard] = Vector.empty,
                   victoryPoints:Int = 0,
                   structures:Map[StructurePlacement, Int] = StructurePlacement.get.map( p => (p, p.available) ).toMap
                 ) {

  def idName:String = "<" + id.id + ">" + name

  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[Player] = resources.subtract( resource, amount ) match {
    case Success( newResources ) => Success( copy( resources = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def removeResourceCards( cards:ResourceCards ):Try[Player] = resources.subtract( cards ) match {
    case Success( newResources ) => Success( copy( resources = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def addResourceCard( resource:Resource, amount:Int = 1 ):Player = copy( resources = resources.add( resource, amount ) )

  def addResourceCards( cards:ResourceCards ):Player = copy( resources = resources.add( cards ) )

  def trade( get:ResourceCards, give:ResourceCards ):Try[Player] = addResourceCards( get ).removeResourceCards( give )

  def addDevCard( card:DevelopmentCard ):Player = copy( devCards = devCards :+ card )

  def removeDevCard():Player = copy( devCards = devCards.init )

  def addVictoryPoint( ):Player = copy( victoryPoints = victoryPoints + 1 )

  def hasStructure( structure:StructurePlacement ):Boolean = structures.getOrElse( structure, 0 ) > 0

  def getStructure( structure:StructurePlacement ):Try[Player] = {
    val available = structures.getOrElse( structure, 0 )
    if ( available > 0 ) {
      val newStructures = if ( structure.replaces.isDefined )
        structures.updated( structure.replaces.get, structures( structure.replaces.get ) + 1 )
      else structures
      Success( copy( structures = newStructures.updated( structure, available - 1 ) ) )
    } else
      Failure( InsufficientStructures( structure ) )
  }

  def addStructure( structure:StructurePlacement ):Player = {
    val newStructures = if( structure.replaces.isDefined )
      structures.updated( structure.replaces.get, structures.getOrElse( structure.replaces.get, 0 ) - 1 )
    else structures
    copy(
      structures = newStructures.updated( structure, newStructures.getOrElse( structure, 0 ) + 1 )
    )
  }

  def randomHandResource():Option[Resource] = {
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
