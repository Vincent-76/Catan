package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.commands.{ BankTradeCommand, BuyDevCardCommand, EndTurnCommand, SetBuildStateCommand, SetPlayerTradeStateCommand, UseDevCardCommand }
import de.htwg.se.catan.model.{ Command, DevelopmentCard, State, StateImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object ActionState extends StateImpl( "ActionState" ) {
  def fromXML( node:Node ):ActionState = ActionState()

  def fromJson( json:JsValue ):State = ActionState()
}

case class ActionState() extends State {

  def toXML:Node = <ActionState />.copy( label = ActionState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ActionState.name )
  )

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
