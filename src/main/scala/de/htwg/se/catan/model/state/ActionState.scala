package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.commands.{ BankTradeCommand, BuyDevCardCommand, EndTurnCommand, SetBuildStateCommand, SetPlayerTradeStateCommand, UseDevCardCommand }
import de.htwg.se.catan.model.{ Command, DevelopmentCard, State, StructurePlacement }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object ActionState {
  def fromXML( node:Node ):ActionState = ActionState()
}

case class ActionState() extends State {

  def toXML:Node = <ActionState />

  override def setBuildState( structure:StructurePlacement ):Option[Command] = Some(
    SetBuildStateCommand( structure, this )
  )

  override def bankTrade( give:ResourceCards, get:ResourceCards ):Option[Command] = Some(
    BankTradeCommand( give, get )
  )

  override def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Option[Command] = Some(
    SetPlayerTradeStateCommand( give, get, this )
  )

  override def buyDevCard( ):Option[Command] = Some( BuyDevCardCommand( this ) )

  override def useDevCard( devCard:DevelopmentCard ):Option[Command] = Some(
    UseDevCardCommand( devCard, this )
  )

  override def endTurn( ):Option[Command] = Some( EndTurnCommand( this ) )

  //override def toString:String = getClass.getSimpleName
}
