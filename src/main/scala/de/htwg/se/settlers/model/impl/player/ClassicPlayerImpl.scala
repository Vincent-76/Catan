package de.htwg.se.settlers.model.impl.player

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Random, Success, Try }

case class ClassicPlayerImpl( idVal:PlayerID,
                              colorVal:PlayerColor,
                              nameVal:String,
                              resourcesVal:ResourceCards = Cards.getResourceCards( 0 ),
                              devCardsVal:Vector[DevelopmentCard] = Vector.empty,
                              usedDevCards:Vector[DevelopmentCard] = Vector.empty,
                              victoryPointsVal:Int = 0,
                              structures:Map[StructurePlacement, Int] = StructurePlacement.all.map( p => (p, p.available) ).toMap
                     ) extends Player {

  def id:PlayerID = idVal
  def name:String = nameVal
  def resources:ResourceCards = resourcesVal
  def color:PlayerColor = colorVal
  def victoryPoints:Int = victoryPointsVal
  def devCards:Vector[DevelopmentCard] = devCardsVal

  def hasResources( resources:ResourceCards ):Boolean = resourcesVal.has( resources )

  def resourceAmount:Int = resourcesVal.amount
  def resourceAmount( resource:Resource ):Int = resourcesVal.getOrElse( resource, 0 )

  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[Player] = resourcesVal.subtract( resource, amount ) match {
    case Success( newResources ) => Success( copy( resourcesVal = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def removeResourceCards( cards:ResourceCards ):Try[Player] = resourcesVal.subtract( cards ) match {
    case Success( newResources ) => Success( copy( resourcesVal = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def addResourceCard( resource:Resource, amount:Int = 1 ):Player = copy( resourcesVal = resourcesVal.add( resource, amount ) )

  def addResourceCards( cards:ResourceCards ):Player = copy( resourcesVal = resourcesVal.add( cards ) )

  def trade( get:ResourceCards, give:ResourceCards ):Try[Player] = addResourceCards( get ).removeResourceCards( give )

  def addDevCard( card:DevelopmentCard, removeFromUsed:Boolean = false ):Player = if( removeFromUsed )
    copy( devCardsVal = devCardsVal :+ card, usedDevCards = usedDevCards.removed( card ).toVector )
  else copy( devCardsVal = devCardsVal :+ card )

  def removeDevCard( ):Player = copy( devCardsVal = devCardsVal.init )

  def usedDevCards( devCard:DevelopmentCard ):Int = usedDevCards.count( _ == devCard )

  def addVictoryPoint( ):Player = copy( victoryPointsVal = victoryPointsVal + 1 )

  def hasStructure( structure:StructurePlacement ):Boolean = structures.getOrElse( structure, 0 ) > 0

  def getStructure( structure:StructurePlacement ):Try[Player] = {
    val available = structures.getOrElse( structure, 0 )
    if( available > 0 ) {
      val newStructures = if( structure.replaces.isDefined )
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

  def randomHandResource( ):Option[Resource] = Random.element( resources.flatMap( d => ( 0 until d._2 ).map( _ => d._1 ) ).toSeq )

  def useDevCard( devCard:DevelopmentCard ):Try[Player] = {
    val index = devCardsVal.indexOf( devCard )
    if( index >= 0 )
      Success( copy(
        devCardsVal = devCardsVal.removeAt( index ),
        usedDevCards = usedDevCards :+ devCard
      ) )
    else Failure( InsufficientDevCards( devCard ) )
  }
}
