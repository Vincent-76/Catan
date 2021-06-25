package de.htwg.se.catan.model

import Card.ResourceCards
import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonParseError, JsonSerializable, XMLDeserializer, XMLParseError, XMLSerializable }
import de.htwg.se.catan.model.state._
import de.htwg.se.catan.util.RichString
import play.api.libs.json.{ JsResult, JsSuccess, JsValue, Json, Reads, Writes }

import scala.xml.Node

/**
 * @author Vincent76;
 */
abstract class StateImpl( name:String ) extends DeserializerComponentImpl[State]( name ) {
  override def init():Unit = State.addImpl( this )
}

object State extends ClassComponent[State, StateImpl] {
  implicit val stateWrites:Writes[State] = ( o:State ) => o.toJson
  implicit val stateReads:Reads[State] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
}

trait State extends XMLSerializable with JsonSerializable {

  def initGame( ):Option[Command] = None

  def addPlayer( playerColor:PlayerColor, name:String ):Option[Command] = None

  def setInitBeginnerState():Option[Command] = None

  def diceOutBeginner():Option[Command] = None

  def setBeginner():Option[Command] = None

  def buildInitSettlement( vID:Int ):Option[Command] = None

  def buildInitRoad( eID:Int ):Option[Command] = None

  def startTurn():Option[Command] = None

  def rollTheDices():Option[Command] = None

  def useDevCard( devCard:DevelopmentCard ):Option[Command] = None

  def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = None

  def placeRobber( hID:Int ):Option[Command] = None

  def robberStealFromPlayer( stealPlayerID:PlayerID ):Option[Command] = None

  def setBuildState( structure:StructurePlacement ):Option[Command] = None

  def build( id:Int ):Option[Command] = None

  def bankTrade( give:ResourceCards, get:ResourceCards ):Option[Command] = None

  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Option[Command] = None

  def playerTradeDecision( decision:Boolean ):Option[Command] = None

  def abortPlayerTrade():Option[Command] = None

  def playerTrade( tradePlayerID:PlayerID ):Option[Command] = None

  def buyDevCard():Option[Command] = None

  def yearOfPlentyAction( resources:ResourceCards ):Option[Command] = None

  def devBuildRoad( eID:Int ):Option[Command] = None

  def monopolyAction( r:Resource ):Option[Command] = None

  def endTurn():Option[Command] = None
}
