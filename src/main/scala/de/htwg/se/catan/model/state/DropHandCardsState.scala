package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.DropHandCardsCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DropHandCardsState {
  def fromXML( node:Node ):DropHandCardsState = DropHandCardsState(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    dropped = node.childOf( "dropped" ).convertToList( n => PlayerID.fromXML( n ) )
  )
}

case class DropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State {

  def toXML:Node = <DropHandCardsState>
    <pID>{ pID.toXML }</pID>
    <dropped>{ dropped.map( pID => pID.toXML ) }</dropped>
  </DropHandCardsState>

  override def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = Some(
    DropHandCardsCommand( this, cards )
  )

  //override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], dropped[" + dropped.mkString( ", " ) + "]"
}
