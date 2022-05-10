package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.*
import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object DiceInfo extends InfoImpl( "DiceInfo" ):
  def fromXML( node:Node ):DiceInfo = DiceInfo(
    dices = node.childOf( "dices" ).asTuple( _.content.toInt, _.content.toInt ),
  )

  def fromJson( json:JsValue ):DiceInfo = DiceInfo(
    dices = ( json \ "dices" ).asTuple[Int, Int],
  )


case class DiceInfo( dices:(Int, Int) ) extends Info:
  def toXML:Node = <DiceInfo>
    <dices>{ dices.toXML }</dices>
  </DiceInfo>.copy( label = DiceInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DiceInfo.name ),
    "dices" -> Json.toJson( dices ),
  )