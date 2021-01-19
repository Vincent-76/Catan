package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.GameField.{ Hex, Row, Vertex }
import de.htwg.se.settlers.model.commands.{ ChangeStateCommand, GatherCommand, UseDevCardCommand }
import de.htwg.se.settlers.model.{ DiceInfo, _ }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
abstract class DiceState( val dices:(Int, Int), controller:Controller ) extends State( controller ) {

  override def useDevCard( devCard:DevelopmentCard ):Unit = controller.action( UseDevCardCommand( devCard, this ) )


  override def rollTheDices( ):Unit = controller.action( Numbers.of( dices._1 + dices._2 ) match {
    case Seven => controller.game.checkHandCardsInOrder() match {
      case Some( p ) => ChangeStateCommand( this, controller.ui.getDropHandCardsState( p.id ), Some( DiceInfo( dices ) ) )
      case None => ChangeStateCommand( this, controller.ui.getRobberPlaceState( controller.ui.getActionState ), Some( DiceInfo( dices ) ) )
    }
    case n:Number => gather( n )
  } )

  private def gather( number:Number ):Command = {
    val playerResources = controller.game.gameField.hexagons.red( Map.empty[PlayerID, ResourceCards],
      ( resources:Map[PlayerID, ResourceCards], row:Row[Hex] ) => row.red( resources, ( resources:Map[PlayerID, ResourceCards], hex:Option[Hex] ) => {
        if ( hex.isDefined && hex.get != controller.game.gameField.robber ) hex.get.area match {
          case r:ResourceArea if r.number == number =>
            controller.game.gameField.adjacentVertices( hex.get ).red( resources, ( resources:Map[PlayerID, ResourceCards], v:Vertex ) =>
              v.building match {
                case Some( v:Settlement ) =>
                  resources.updated( v.owner, resources.getOrElse( v.owner, Cards.getResourceCards( 0 ) ).add( r.resource, 1 ) )
                case Some( c:City ) =>
                  resources.updated( c.owner, resources.getOrElse( c.owner, Cards.getResourceCards( 0 ) ).add( r.resource, 2 ) )
                case _ => resources
              } )
          case _ => resources
        }
        else resources
      } ) )
    val (availablePlayerResources, _) = playerResources.red( (playerResources, controller.game.resourceStack),
      ( data:(Map[PlayerID, ResourceCards], ResourceCards), pID:PlayerID, cards:ResourceCards ) => {
        val (available, newStack) = controller.game.getAvailableResourceCards( cards, data._2 )
        if ( available.amount > 0 )
          (data._1.updated( pID, available ), newStack)
        else
          (data._1 - pID, data._2)
      }
    )
    GatherCommand( this, availablePlayerResources )
  }

}
