package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.impl.placement.RoadPlacement
import de.htwg.se.catan.model.error.{ AlreadyUsedDevCardInTurn, DevCardDrawnInTurn, InsufficientStructures, NoPlacementPoints }
import de.htwg.se.catan.model.state.{ DevRoadBuildingState, MonopolyState, RobberPlaceState, YearOfPlentyState }
import de.htwg.se.catan.util.*
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object UseDevCardCommand extends CommandImpl( "UseDevCardCommand" ):
  override def fromXML( node:Node ):UseDevCardCommand = UseDevCardCommand(
    devCard = DevelopmentCard.of( ( node \ "@devCard" ).content ).get,
    state = State.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):UseDevCardCommand = UseDevCardCommand(
    devCard = ( json \ "devCard" ).as[DevelopmentCard],
    state = ( json \ "state" ).as[State]
  )


case class UseDevCardCommand( devCard:DevelopmentCard, state:State ) extends Command:

  def toXML:Node = <UseDevCardCommand devCard={ devCard.title }>
    <state>{ state.toXML }</state>
  </UseDevCardCommand>.copy( label = UseDevCardCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( UseDevCardCommand.name ),
    "devCard" -> Json.toJson( devCard ),
    "state" -> state.toJson
  )

  var actualBonusCards:Option[Map[BonusCard, Option[(PlayerID, Int)]]] = None

  override def doStep( game:Game ):Try[CommandSuccess] =
    if game.turn.usedDevCard then
      return Failure( AlreadyUsedDevCardInTurn )
    val newPlayer = game.player.useDevCard( devCard )
    if newPlayer.isFailure then
      return newPlayer.rethrow
    if game.player.devCards.count( _ == devCard ) <= game.turn.drawnDevCards.count( _ == devCard ) then
      return Failure( DevCardDrawnInTurn( devCard ) )
    val nextState = devCard match
      case KnightCard => RobberPlaceState( state )
      case YearOfPlentyCard => YearOfPlentyState( state )
      case RoadBuildingCard =>
        if !game.player.hasStructure( RoadPlacement ) then
          return Failure( InsufficientStructures( RoadPlacement ) )
        if RoadPlacement.getBuildablePoints( game, game.onTurn ).isEmpty then
          return Failure( NoPlacementPoints( RoadPlacement ) )
        DevRoadBuildingState( state )
      case MonopolyCard => MonopolyState( state )
      //case _ => state
    val largestArmyValue = if devCard == KnightCard then
      val amount = newPlayer.get.usedDevCards( KnightCard )
      val largestArmy = game.bonusCards( LargestArmyCard )
      if amount >= LargestArmyCard.required && (largestArmy.isEmpty || amount > largestArmy.get._2) then
        Some( newPlayer.get.id, amount )
      else game.bonusCard( LargestArmyCard )
    else game.bonusCard( LargestArmyCard )
    actualBonusCards = Some( game.bonusCards )
    success( game.setState( nextState )
      .setTurn( game.turn.setUsedDevCard( true ) )
      .updatePlayer( newPlayer.get )
      .setBonusCard( LargestArmyCard, largestArmyValue )
    )

  override def undoStep( game:Game ):Game = game.setState( state )
    .updatePlayer( game.player.addDevCard( devCard, removeFromUsed = true ) )
    .setTurn( game.turn.setUsedDevCard( false ) )
    .setBonusCards( actualBonusCards.getOrElse( game.bonusCards ) )

  //override def toString:String = getClass.getSimpleName + ": devCard[" + devCard + "], " + state
