package de.htwg.se.catan.model

import de.htwg.se.catan.model.Cards._
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.impl.fileio.XMLSerializable
import de.htwg.se.catan.util._

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerID {
  def fromXML( node:Node ):PlayerID = PlayerID(
    id = ( node \ "@id" ).content.toInt
  )
}

case class PlayerID /*private[Game]*/ ( id:Int ) extends XMLSerializable {

  def toXML:Node = <PlayerID id={ id.toString } />

  override def toString:String = id.toString;
}

object PlayerColor {
  val all = List(
    Green,
    Blue,
    Yellow,
    Red
  )

  def colorOf( cString:String ):Option[PlayerColor] = all.find( _.name.toLowerCase == cString.toLowerCase() )

  def availableColors( players:Iterable[PlayerColor] = Vector.empty ):Seq[PlayerColor] = {
    players.red( all, ( c:Seq[PlayerColor], p:PlayerColor ) => {
      c.removed( p )
    } )
  }
}

sealed abstract class PlayerColor( val name:String )

case object Green extends PlayerColor( "Green" )

case object Blue extends PlayerColor( "Blue" )

case object Yellow extends PlayerColor( "Yellow" )

case object Red extends PlayerColor( "Red" )


trait PlayerFactory {
  def create( pID:PlayerID, color:PlayerColor, name:String ):Player
}

object Player {

}

trait Player extends XMLSerializable {

  def id:PlayerID
  def name:String
  def resources:ResourceCards
  def color:PlayerColor
  def victoryPoints:Int
  def devCards:Vector[DevelopmentCard]
  def usedDevCards:Vector[DevelopmentCard]

  def idName:String = "<" + id.id + ">" + name

  def hasResources( resources:ResourceCards ):Boolean
  def resourceAmount:Int
  def resourceAmount( resource:Resource ):Int
  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[Player]
  def removeResourceCards( cards:ResourceCards ):Try[Player]
  def addResourceCard( resource:Resource, amount:Int = 1 ):Player
  def addResourceCards( cards:ResourceCards ):Player
  def trade( get:ResourceCards, give:ResourceCards ):Try[Player]
  def addDevCard( card:DevelopmentCard, removeFromUsed:Boolean = false ):Player
  def removeLastDevCard( ):Player
  def usedDevCards( devCard:DevelopmentCard ):Int
  def addVictoryPoint( ):Player
  def hasStructure( structure:StructurePlacement ):Boolean
  def getStructure( structure:StructurePlacement ):Try[Player]
  def addStructure( structure:StructurePlacement ):Player
  def randomHandResource( ):Option[Resource]
  def useDevCard( devCard:DevelopmentCard ):Try[Player]
}
