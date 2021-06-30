package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State, StateImpl }
import de.htwg.se.catan.model.commands.{ DiceOutBeginnerCommand, SetBeginnerCommand }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLOption }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitBeginnerState extends StateImpl( "InitBeginnerState" ) {
  def fromXML( node:Node ):InitBeginnerState = InitBeginnerState(
    beginner = node.childOf( "beginner" ).asOption( n => PlayerID.fromXML( n ) ),
    diceValues = node.childOf( "diceValues" ).asMap( n => PlayerID.fromXML( n ), _.content.toInt ),
    counter = ( node \ "@counter" ).content.toInt
  )

  def fromJson( json:JsValue ):InitBeginnerState = InitBeginnerState(
    beginner = ( json \ "beginner" ).asOption[PlayerID],
    diceValues = ( json \ "diceValues" ).asMap[PlayerID, Int],
    counter = ( json \ "counter" ).as[Int]
  )
}

case class InitBeginnerState( beginner:Option[PlayerID] = None,
                              diceValues:Map[PlayerID, Int] = Map.empty,
                              counter:Int = 1 ) extends State {

  def toXML:Node = <InitBeginnerState counter={ counter.toString }>
    <beginner>{ beginner.toXML( _.toXML ) }</beginner>
    <diceValues>{ diceValues.toXML( _.toXML, _.toString ) }</diceValues>
  </InitBeginnerState>.copy( label = InitBeginnerState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InitBeginnerState.name ),
    "beginner" -> Json.toJson( beginner ),
    "diceValues" -> Json.toJson( diceValues ),
    "counter" -> Json.toJson( counter )
  )

  override def diceOutBeginner():Option[Command] = Some(
    DiceOutBeginnerCommand( this )
  )

  override def setBeginner():Option[Command] = Some(
    SetBeginnerCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": beginner[" + beginner.useOrElse( pID => pID, "-" ) +
    "], DiceValues[" + diceValues.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "], counter[" + counter + "]"*/

}
