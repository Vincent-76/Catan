package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.{ JsonSerializable, XMLSerializable }
import play.api.libs.json.{ JsSuccess, JsValue, Reads, Writes }

/**
 * @author Vincent76;
 */
abstract class TurnImpl( name:String ) extends DeserializerComponentImpl[Turn]( name ):
  override def init():Unit = Turn.addImpl( this )


object Turn extends ClassComponent[Turn, TurnImpl]:
  given turnWrites:Writes[Turn] = ( o:Turn ) => o.toJson
  given turnReads:Reads[Turn] = ( json:JsValue ) => JsSuccess( fromJson( json ) )


trait Turn extends XMLSerializable with JsonSerializable:

  def playerID:PlayerID

  def usedDevCard:Boolean

  def setUsedDevCard( used:Boolean ):Turn

  def drawnDevCards:List[DevelopmentCard]

  def addDrawnDevCard( card:DevelopmentCard ):Turn

  def removeDrawnDevCard( ):Turn

  def getLastDrawnDevCard:Option[DevelopmentCard]

  def set( playerID:PlayerID ):Turn
