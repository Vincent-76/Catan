package de.htwg.se.catan.model.impl.turn

import com.google.inject.Inject
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLSequence }
import de.htwg.se.catan.model.{ Cards, DevelopmentCard, PlayerID, Turn }

import scala.xml.Node

object ClassicTurnImpl {
  def fromXML( node:Node ):ClassicTurnImpl = ClassicTurnImpl(
    playerIDVal = PlayerID.fromXML( node.childOf( "playerID" ) ),
    usedDevCardVal = ( node \ "@usedDevCard" ).content.toBoolean,
    drawnDevCardsVal = node.childOf( "drawnDevCards" ).convertToList( n => Cards.devCardOf( n.content ).get )
  )
}

case class ClassicTurnImpl( playerIDVal:PlayerID = new PlayerID( -1 ),
                            usedDevCardVal:Boolean = false,
                            drawnDevCardsVal:List[DevelopmentCard] = List.empty ) extends Turn {

  @Inject
  def this( ) = this( new PlayerID( -1 ) )

  def toXML:Node = <ClassicTurnImpl usedDevCard={ usedDevCardVal.toString }>
    <playerID>{ playerIDVal.toXML }</playerID>
    <drawnDevCards>{ drawnDevCardsVal.toXML( _.title ) }</drawnDevCards>
  </ClassicTurnImpl>

  def playerID:PlayerID = playerIDVal

  def usedDevCard:Boolean = usedDevCardVal

  def setUsedDevCard( used:Boolean ):Turn = copy( usedDevCardVal = used )

  def drawnDevCards:List[DevelopmentCard] = drawnDevCardsVal

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal :+ card )

  def removeDrawnDevCard( ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCardsVal.lastOption

  def set( playerID:PlayerID ):Turn = ClassicTurnImpl( playerID )

}
