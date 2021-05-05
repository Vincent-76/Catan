package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.{ResourceCards, developmentCardCost}
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.{Blue, Green, Red, Yellow}
import de.htwg.se.settlers.model.state.{ActionState, InitState}
import de.htwg.se.settlers.util._
import org.scalatest.{Matchers, WordSpec}

import scala.collection.immutable.{SortedMap, TreeMap}
import scala.util.{Failure, Random, Success}

/**
 * @author Vincent76;
 */
class GameSpec extends WordSpec with Matchers {
  "Game" when {
    val newGame:Game = Game( InitState( null ), test = true )
    val randomGame = Game( InitState( null ) )
    "random new" should {
      "have state" in {
        randomGame.state shouldBe a [InitState]
      }
      "have no winner" in {
        randomGame.winner shouldBe None
      }
      "have round" in {
        randomGame.round shouldBe 1
      }
      "have on turn" in {
        newGame.onTurn.id shouldBe -1
      }
    }
    "new" should {
      "have state" in {
        newGame.state shouldBe a [InitState]
      }
      "have game field" in {
        newGame.gameField.robber.id shouldBe 20
      }
      "have no winner" in {
        newGame.winner shouldBe None
      }
      "have round" in {
        newGame.round shouldBe 1
      }
      "have seed" in {
        newGame.seed shouldBe 1
      }
      "have on turn" in {
        newGame.onTurn.id shouldBe -1
      }
      "addPlayer" in {
        val game = newGame.addPlayer( Green, "A" )
        game.isPlayer( 0 ) shouldBe true
        val playerID = game.getPlayerID( 0 )
        playerID shouldNot be( None )
        playerID.get.toString shouldBe "0"
        val player = game.player( playerID.get )
        player.name shouldBe "A"
        player.color shouldBe Green
        game.addPlayerF( Green, "A" ) shouldBe Failure( PlayerColorIsAlreadyInUse( Green ) )
        game.addPlayerF( Blue, "A" ) shouldBe Failure( PlayerNameAlreadyExists( "A" ) )
        game.addPlayerF( Blue, "B" ) shouldNot be( None )
        game.players should have size 1
      }
    }
    val game = addCleanPlayers( newGame )
    val pID = game.onTurn
    "used" should {
      "removeLastPlayer" in {
        game.removeLastPlayer().players should have size 2
      }
      "check turn" in {
        game.turn.playerID.id shouldBe 0
        game.player.id.id shouldBe 0
        game.nextTurn().id shouldBe 1
        game.previousTurn().id shouldBe 2
        game.nextPlayer().name shouldBe "B"
        game.previousPlayer().name shouldBe "C"
      }
      "updatePlayer" in {
        game.updatePlayer( game.player().copy( name = "AA" ) ).player().name shouldBe "AA"
      }
      "updatePlayers" in {
        val player1ID = game.getPlayerID( 1 )
        player1ID shouldNot be( None )
        val player2ID = game.getPlayerID( 2 )
        player2ID shouldNot be( None )
        val players = game.updatePlayers(
          game.player( player1ID.get ).copy( name = "BB" ),
          game.player( player2ID.get ).copy( name = "CC" )
        )
        players should have size 3
        players.get( player1ID.get ) shouldNot be( None )
        players( player1ID.get ).name shouldBe "BB"
        players.get( player2ID.get ) shouldNot be( None )
        players( player2ID.get ).name shouldBe "CC"
      }
      "setState" in {
        game.setState( ActionState( null ) ).state shouldBe a [ActionState]
      }
      "rollDice" in {
        game.rollDice( new Random() ) should ( be >= 1 and be <= 6 )
      }
      "rollDices" in {
        val dices = game.rollDices()
        dices._1 should ( be >= 1 and be <= 6 )
        dices._2 should ( be >= 1 and be <= 6 )
      }
      "getAvailableResourceCards" in {
        val availableCards = newGame.getAvailableResourceCards( ResourceCards.of( wood = 2, wheat = 15, ore = 25 ) )
        availableCards shouldBe (
          ResourceCards.of( wood = 2, wheat = 15, ore = 19 ),
          ResourceCards.of( wood = 17, clay = 19, sheep = 19, wheat = 4 )
        )
      }
      "drawResourceCards" in {
        Success( game.drawResourceCards( pID, Wood ).resourceStack ) shouldBe game.resourceStack.subtract( Wood )
        Success( game.drawResourceCards( pID, Wood, 2 ).resourceStack ) shouldBe game.resourceStack.subtract( Wood, 2 )
        Success( game.drawResourceCards( pID, ResourceCards.of( wood = 2 ) ).resourceStack ) shouldBe game.resourceStack.subtract( ResourceCards.of( wood = 2 ) )
        val game4 = game.drawResourceCards( pID, Wood, game.resourceStack( Wood ) )
        game4.drawResourceCards( pID, Wood ) shouldBe game4
      }
      "dropResourceCards" in {
        val game2 = game.updatePlayer( game.player().addResourceCard( Wood, 3 ) )
        val result1 = game2.dropResourceCards( pID, Wood )
        result1 shouldBe a [Success[Game]]
        result1.get.resourceStack( Wood ) shouldBe game2.resourceStack( Wood ) + 1
        game2.dropResourceCards( pID, Wood, 4 ) shouldBe Failure( InsufficientResources )
        game2.dropResourceCards( pID, ResourceCards.of( wood = 4 ) ) shouldBe Failure( InsufficientResources )

      }
      "drawDevCard" in {
        game.drawDevCard( pID ) shouldBe Failure( InsufficientResources )
        val game1 = game.drawResourceCards( pID, developmentCardCost )
        val game2 = game1.drawDevCard( pID )
        game2 shouldBe a [Success[Game]]
        game2.get.player().devCards should have size 1
        game2.get.player().devCards.head shouldBe game.developmentCards.head
        game2.get.player().resources.amount shouldBe 0
        game2.get.resourceStack shouldBe game.resourceStack
        val game3 = game.copy( developmentCards = List.empty )
        game3.drawDevCard( pID ) shouldBe Failure( DevStackIsEmpty )
      }
      "updateGameField" in {
        val vertex = game.gameField.vertices.values.head
        val nGameField = game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) )
        val game2 = game.updateGameField( nGameField )
        game2.gameField shouldBe nGameField
        game2.gameField.vertices.values.head.building shouldBe a [Some[Settlement]]
      }
      "settlementAmount" in {
        game.settlementAmount( pID ) shouldBe 0
        val vertex = game.gameField.vertices.values.head
        val nGameField = game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) )
        val game2 = game.updateGameField( nGameField )
        game2.settlementAmount( pID ) shouldBe 1
      }
      "roadAmount" in {
        game.roadAmount( pID ) shouldBe 0
        val edge = game.gameField.edges.values.head
        val nGameField = game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
        val game2 = game.updateGameField( nGameField )
        game2.roadAmount( pID ) shouldBe 1
      }
      "noBuildingInRange" in {
        val vertex = game.gameField.findVertex( 10 )
        vertex shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 11 )
        vertex2 shouldNot be( None )
        game.noBuildingInRange( vertex.get ) shouldBe true
        val game2 = game.updateGameField( game.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.noBuildingInRange( vertex.get ) shouldBe false
      }
      "playerHasAdjacentEdge" in {
        val edge = game.gameField.edges.values.head
        game.playerHasAdjacentEdge( pID, List( edge ) ) shouldBe false
        val game2 = game.updateGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        game2.playerHasAdjacentEdge( pID, List( game2.gameField.edges.values.head ) ) shouldBe true
      }
      "playerHasAdjacentVertex" in {
        val vertex = game.gameField.vertices.values.head
        game.playerHasAdjacentVertex( pID, List( vertex ) ) shouldBe false
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.playerHasAdjacentVertex( pID, List( game2.gameField.vertices.values.head ) ) shouldBe true
      }
      "roadBuildable" in {
        val edge = game.gameField.findEdge( 17 )
        edge shouldNot be( None )
        game.roadBuildable( edge.get, pID ) shouldBe false
        val edge2 = game.gameField.adjacentEdges( edge.get ).head.setRoad( Some( Road( pID ) ) )
        val game2 = game.updateGameField( game.gameField.update( edge2 ) )
        game2.roadBuildable( edge.get, pID ) shouldBe true
        val vertex = game.gameField.adjacentVertices( edge.get ).head.setBuilding( Some( Settlement( pID ) ) )
        val game3 = game.updateGameField( game.gameField.update( vertex ) )
        game3.roadBuildable( edge.get, pID ) shouldBe true
      }
      "roadLength" in {
        val edge = game.gameField.edges.values.head
        game.roadLength( pID, edge ) shouldBe 0
        val edge2 = edge.setRoad( Some( Road( pID ) ) )
        val edge3 = game.gameField.adjacentEdges( edge2 ).head.setRoad( Some( Road( pID ) ) )
        val game2 = game.updateGameField( game.gameField.update( edge2 ).update( edge3 ) )
        game2.roadLength( pID, edge2, 0, None ) shouldBe 2
      }
      "checkHandCardsInOrder" in {
        val game2 = game.drawResourceCards( pID, Wood, Game.maxHandCards + 1 )
        game2.checkHandCardsInOrder() shouldBe Some( game2.player )
        val pID1 = game.getPlayerID( 1 )
        pID1 shouldNot be( None )
        val game3 = game.drawResourceCards( pID1.get, Wood, Game.maxHandCards + 1 )
        game3.checkHandCardsInOrder( game3.player, List.empty ) shouldBe Some( game3.player( pID1.get ) )
      }
      "getBuildableRoadSpotsForSettlement" in {
        val vID = 1
        game.getBuildableRoadSpotsForSettlement( vID ).map( _.id ) shouldBe empty
        val vertex = game.gameField.findVertex( vID )
        vertex shouldNot be( None )
        val game2 = game.updateGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.getBuildableRoadSpotsForSettlement( vID ).map( _.id ) should contain theSameElementsAs List( 1, 3 )
      }
      "getBankTradeFactor" in {
        game.getBankTradeFactor( pID, Wood ) shouldBe Game.defaultBankTradeFactor
        val vertex = game.gameField.findVertex( 8 )
        vertex shouldNot be( None )
        val game2 = game.updateGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.getBankTradeFactor( pID, Wood ) shouldBe Game.unspecifiedPortFactor
        val vertex2 = game2.gameField.findVertex( 16 )
        vertex2 shouldNot be( None )
        val game3 = game2.updateGameField( game2.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game3.getBankTradeFactor( pID, Wood ) shouldBe Game.specifiedPortFactor
      }
      "getNextTradePlayerInOrder" in {
        val pID1 = game.getPlayerID( 1 )
        pID1 shouldNot be( None )
        val pID2 = game.getPlayerID( 2 )
        pID2 shouldNot be( None )
        game.getNextTradePlayerInOrder( Map.empty[PlayerID, Boolean], pID1.get ) shouldBe Some( pID1.get )
        game.getNextTradePlayerInOrder( Map( pID1.get -> false ) ) shouldBe Some( pID2.get )
        game.getNextTradePlayerInOrder( Map( pID1.get -> false, pID2.get -> false ) ) shouldBe None
      }
    }
  }

  private def addCleanPlayers( game:Game ):Game = {
    val nGame = game.addPlayer( Green, "A" )
      .addPlayer( Blue, "B" )
      .addPlayer( Yellow, "C" )
    nGame.copy( turn = Turn( nGame.getPlayerID( 0 ).get ) )
  }
}
