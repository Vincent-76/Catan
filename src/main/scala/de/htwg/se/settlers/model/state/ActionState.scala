package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.commands.{ BankTradeCommand, BuyDevCardCommand, SetBuildStateCommand, SetPlayerTradeStateCommand, EndTurnCommand, UseDevCardCommand }
import de.htwg.se.settlers.model.{ DevelopmentCard, Resource, State, StructurePlacement }

/**
 * @author Vincent76;
 */
case class ActionState( controller:Controller ) extends State( controller ) {

  override def setBuildState( structure:StructurePlacement ):Unit = controller.action(
    SetBuildStateCommand( structure, this )
  )

  override def bankTrade( give:ResourceCards, get:ResourceCards ):Unit = controller.action(
    BankTradeCommand( give, get )
  )

  override def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = controller.action(
    SetPlayerTradeStateCommand( give, get, this )
  )

  override def buyDevCard( ):Unit = controller.action( BuyDevCardCommand( this ) )

  override def useDevCard( devCard:DevelopmentCard ):Unit = controller.action(
    UseDevCardCommand( devCard, this )
  )

  override def endTurn( ):Unit = controller.action( EndTurnCommand( this ) )

}
