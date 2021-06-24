package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.{ DiceOutBeginnerCommand, SetBeginnerCommand }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLOption }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitBeginnerState {
  def fromXML( node:Node ):InitBeginnerState = InitBeginnerState(
    beginner = node.childOf( "beginner" ).toOption( n => PlayerID.fromXML( n ) ),
    diceValues = node.childOf( "diceValues" ).convertToMap( n => PlayerID.fromXML( n ), _.content.toInt ),
    counter = ( node \ "@counter" ).content.toInt
  )
}

case class InitBeginnerState( beginner:Option[PlayerID] = None,
                              diceValues:Map[PlayerID, Int] = Map.empty,
                              counter:Int = 1 ) extends State {

  def toXML:Node = <InitBeginnerState counter={ counter.toString }>
    <beginner>{ beginner.toXML( _.toXML ) }</beginner>
    <diceValues>{ diceValues.toXML( _.toXML, _.toString ) }</diceValues>
  </InitBeginnerState>

  override def diceOutBeginner():Option[Command] = Some(
    DiceOutBeginnerCommand( this )
  )

  override def setBeginner():Option[Command] = Some(
    SetBeginnerCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": beginner[" + beginner.useOrElse( pID => pID, "-" ) +
    "], DiceValues[" + diceValues.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "], counter[" + counter + "]"*/

}
