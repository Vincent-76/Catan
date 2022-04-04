package de.htwg.se.catan.model

import de.htwg.se.catan.model.Card.*
import de.htwg.se.catan.model.impl.fileio.XMLFileIO._
import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonSerializable, XMLDeserializer, XMLSerializable }
import de.htwg.se.catan.util.*
import play.api.libs.json.*

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerID extends XMLDeserializer[PlayerID] with JsonDeserializer[PlayerID]:
  given playerIDWrites:Writes[PlayerID] = ( o:PlayerID ) => o.toJson
  given playerIDReads:Reads[PlayerID] =( json:JsValue ) => JsSuccess( fromJson( json ) )
  
  def fromXML( node:Node ):PlayerID = PlayerID(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):PlayerID = PlayerID(
    id = ( json  \ "id" ).as[Int]
  )
  

case class PlayerID /*private[Game]*/ ( id:Int ) extends XMLSerializable with JsonSerializable:

  def toXML:Node = <PlayerID id={ id.toString } />

  def toJson:JsValue = Json.obj(
    "id" -> Json.toJson( id )
  )

  override def toString:String = id.toString;


object PlayerColor extends ObjectComponent[PlayerColor]:
  given playerColorWrites:Writes[PlayerColor] = ( playerColor:PlayerColor ) => Json.toJson( playerColor.title )
  given playerColorReads:Reads[PlayerColor] = ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  Green.init()
  Blue.init()
  Yellow.init()
  Red.init()

  def of( cString:String ):Option[PlayerColor] = impls.find( _.title ^= cString )

  def availableColors( players:Iterable[PlayerColor] = Vector.empty ):Seq[PlayerColor] =
    players.red( impls.toList.sortBy( _.title ), ( c:Seq[PlayerColor], p:PlayerColor ) => {
      c.removed( p )
    } )


sealed abstract class PlayerColor( val title:String ) extends ComponentImpl:
  override def init():Unit = PlayerColor.addImpl( this )


case object Green extends PlayerColor( "Green" )

case object Blue extends PlayerColor( "Blue" )

case object Yellow extends PlayerColor( "Yellow" )

case object Red extends PlayerColor( "Red" )


trait PlayerFactory:
  def create( pID:PlayerID, color:PlayerColor, name:String ):Player


abstract class PlayerImpl( name:String ) extends DeserializerComponentImpl[Player]( name ):
  override def init():Unit = Player.addImpl( this )


object Player extends ClassComponent[Player, PlayerImpl]:
  given turnWrites:Writes[Player] = ( o:Player ) => o.toJson
  given turnReads:Reads[Player] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
  

trait Player extends XMLSerializable with JsonSerializable:

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

