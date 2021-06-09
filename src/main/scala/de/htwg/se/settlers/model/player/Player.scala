package de.htwg.se.settlers.model.player

import de.htwg.se.settlers.model.cards.Cards._
import de.htwg.se.settlers.model.cards.DevelopmentCard
import de.htwg.se.settlers.model.{Resource, StructurePlacement}
import de.htwg.se.settlers.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
object Player {

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

trait Player {

  def idName:String

  def color:PlayerColor

  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[Player]

  def removeResourceCards( cards:ResourceCards ):Try[Player]

  def addResourceCard( resource:Resource, amount:Int = 1 ):Player

  def addResourceCards( cards:ResourceCards ):Player

  def trade( get:ResourceCards, give:ResourceCards ):Try[Player]

  def addDevCard( card:DevelopmentCard ):Player

  def removeDevCard():Player

  def addVictoryPoint():Player

  def hasStructure( structure:StructurePlacement ):Boolean

  def getStructure( structure:StructurePlacement ):Try[Player]

  def addStructure( structure:StructurePlacement ):Player

  def randomHandResource():Option[Resource]

  def useDevCard( devCard:DevelopmentCard ):Try[Player]
}
