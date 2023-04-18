package de.htwg.se.catan.model.impl.turn

import com.google.inject.Inject
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLSequence }
import de.htwg.se.catan.model.{ DevelopmentCard, PlayerID, Turn, TurnImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

object ClassicTurnImpl extends TurnImpl( "ClassicTurnImpl" ):
  def fromXML( node:Node ):ClassicTurnImpl = ClassicTurnImpl(
    playerIDVal = PlayerID.fromXML( node.childOf( "playerID" ) ),
    usedDevCardVal = ( node \ "@usedDevCard" ).content.toBoolean,
    drawnDevCardsVal = node.childOf( "drawnDevCards" ).asList( n => DevelopmentCard.of( n.content ).get )
  )

  def fromJson( json:JsValue ):ClassicTurnImpl = ClassicTurnImpl(
    playerIDVal = ( json \ "playerID" ).as[PlayerID],
    usedDevCardVal = ( json \ "usedDevCard" ).as[Boolean],
    drawnDevCardsVal = ( json \ "drawnDevCards" ).asList[DevelopmentCard]
  )


case class ClassicTurnImpl( playerIDVal:PlayerID = PlayerID( -1 ),
                            usedDevCardVal:Boolean = false,
                            drawnDevCardsVal:List[DevelopmentCard] = List.empty ) extends Turn:

  @Inject
  def this( ) = this( PlayerID( -1 ) )

  def toXML:Node = <ClassicTurnImpl usedDevCard={ usedDevCardVal.toString }>
    <playerID>{ playerIDVal.toXML }</playerID>
    <drawnDevCards>{ drawnDevCardsVal.toXML( _.title ) }</drawnDevCards>
  </ClassicTurnImpl>.copy( label = ClassicTurnImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ClassicTurnImpl.name ),
    "playerID" -> Json.toJson( playerIDVal ),
    "usedDevCard" -> Json.toJson( usedDevCardVal ),
    "drawnDevCards" -> Json.toJson( drawnDevCardsVal )
  )

  def playerID:PlayerID = playerIDVal

  def usedDevCard:Boolean = usedDevCardVal

  def setUsedDevCard( used:Boolean ):Turn = copy( usedDevCardVal = used )

  def drawnDevCards:List[DevelopmentCard] = drawnDevCardsVal

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal :+ card )

  def removeDrawnDevCard( ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCardsVal.lastOption

  def set( playerID:PlayerID ):Turn = ClassicTurnImpl( playerID )
