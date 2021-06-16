package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model.impl.game.ClassicGameImpl
import de.htwg.se.settlers.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.settlers.model.state.{ ActionState, InitState }
import de.htwg.se.settlers.util._
import org.scalatest.{ Matchers, WordSpec }

import scala.util.{ Failure, Random, Success }

/**
 * @author Vincent76;
 */
class ClassicGameImplSpec extends WordSpec with Matchers {
  "ClassicGameImpl" when {
    val newGame:ClassicGameImpl = ClassicGameImpl( test = true, ClassicGameFieldImpl() )
    val randomGame:ClassicGameImpl = ClassicGameImpl( test = false, ClassicGameFieldImpl() )
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
    val game = newGame.addPlayer( Green, "A" )
      .addPlayer( Blue, "B" )
      .addPlayer( Yellow, "C" )
      .use( g => g.setTurn( g.turn.set( g.getPlayerID( 0 ).get ) ) )
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
      "setState" in {
        game.setState( ActionState() ).state shouldBe a [ActionState]
      }
      "setGameField" in {
        val nGameField = ClassicGameFieldImpl()
        game.setGameField( nGameField ).gameField shouldBe nGameField
      }
      "setResourceStack" in {
        val nResourceStack = ResourceCards.of( 1, 2, 3, 4, 5 )
        game.setResourceStack( nResourceStack ).resourceStack shouldBe nResourceStack
      }
      "setDevelopmentCards" in {
        val nDevCards = List( KnightCard, GreatHallCard )
        game.setDevelopmentCards( nDevCards ).developmentCards shouldBe nDevCards
      }
      "updatePlayer" in {
        game.updatePlayer( game.player.addVictoryPoint() ).player.victoryPoints shouldBe 1
      }
      "updatePlayers" in {
        val player1ID = game.getPlayerID( 1 )
        player1ID shouldNot be( None )
        val player2ID = game.getPlayerID( 2 )
        player2ID shouldNot be( None )
        val newGame = game.updatePlayers(
          game.player( player1ID.get ).addVictoryPoint(),
          game.player( player2ID.get ).addVictoryPoint()
        )
        newGame.players should have size 3
        newGame.player( player1ID.get ) shouldNot be( None )
        newGame.player( player1ID.get ).victoryPoints shouldBe 1
        newGame.player( player2ID.get ) shouldNot be( None )
        newGame.player( player2ID.get ).victoryPoints shouldBe 1
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
        Success( game.drawResourceCards( pID, Wood )._1.resourceStack ) shouldBe game.resourceStack.subtract( Wood )
        Success( game.drawResourceCards( pID, Wood, 2 )._1.resourceStack ) shouldBe game.resourceStack.subtract( Wood, 2 )
        Success( game.drawResourceCards( pID, ResourceCards.of( wood = 2 ) )._1.resourceStack ) shouldBe game.resourceStack.subtract( ResourceCards.of( wood = 2 ) )
        val game4 = game.drawResourceCards( pID, Wood, game.resourceStack( Wood ) )._1
        game4.drawResourceCards( pID, Wood )._1 shouldBe game4
      }
      "dropResourceCards" in {
        val game2 = game.updatePlayer( game.player.addResourceCard( Wood, 3 ) )
        val result1 = game2.dropResourceCards( pID, Wood )
        result1 shouldBe a [Success[_]]
        result1.get.resourceStack( Wood ) shouldBe game2.resourceStack( Wood ) + 1
        game2.dropResourceCards( pID, Wood, 4 ) shouldBe Failure( InsufficientResources )
        game2.dropResourceCards( pID, ResourceCards.of( wood = 4 ) ) shouldBe Failure( InsufficientResources )

      }
      "drawDevCard" in {
        game.drawDevCard( pID ) shouldBe Failure( InsufficientResources )
        val game1 = game.drawResourceCards( pID, developmentCardCost )._1
        val game2 = game1.drawDevCard( pID )
        game2 shouldBe a [Success[_]]
        game2.get.player.devCards should have size 1
        game2.get.player.devCards.head shouldBe game.developmentCards.head
        game2.get.player.resources.amount shouldBe 0
        game2.get.resourceStack shouldBe game.resourceStack
        val game3 = game.copy( developmentCards = List.empty )
        game3.drawDevCard( pID ) shouldBe Failure( DevStackIsEmpty )
      }
      "updateGameField" in {
        val vertex = game.gameField.vertexList.head
        val nGameField = game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) )
        val game2 = game.setGameField( nGameField )
        game2.gameField shouldBe nGameField
        game2.gameField.vertexList.head.building shouldNot be( None )
      }
      "getPlayerBonusCards" in {
        game.getPlayerBonusCards( pID ) shouldBe empty
        val nGame = game.setBonusCard( LongestRoadCard, Some( pID, 5 ) )
        nGame.getPlayerBonusCards( pID ) should contain( LongestRoadCard )
      }
      "getPlayerVictoryPoints" in {
        game.getPlayerDisplayVictoryPoints( pID ) shouldBe 0
        val p = (0 until 3).red( game.player, ( p:Player, _ ) => p.addVictoryPoint() ).addDevCard( GreatHallCard ).addDevCard( GreatHallCard )
        val nGame = game.setBonusCard( LongestRoadCard, Some( pID, 5 ) ).updatePlayer( p )
        nGame.getPlayerDisplayVictoryPoints( pID ) shouldBe 5
        nGame.getPlayerVictoryPoints( pID ) shouldBe 7
      }
      "settlementAmount" in {
        game.settlementAmount( pID ) shouldBe 0
        val vertex = game.gameField.vertexList.head
        val nGameField = game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) )
        val game2 = game.setGameField( nGameField )
        game2.settlementAmount( pID ) shouldBe 1
      }
      "roadAmount" in {
        game.roadAmount( pID ) shouldBe 0
        val edge = game.gameField.edgeList.head
        val nGameField = game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) )
        val game2 = game.setGameField( nGameField )
        game2.roadAmount( pID ) shouldBe 1
      }
      "noBuildingInRange" in {
        val vertex = game.gameField.findVertex( 10 )
        vertex shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 11 )
        vertex2 shouldNot be( None )
        game.noBuildingInRange( vertex.get ) shouldBe true
        val game2 = game.setGameField( game.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.noBuildingInRange( vertex.get ) shouldBe false
      }
      "playerHasAdjacentEdge" in {
        val edge = game.gameField.edgeList.head
        game.playerHasAdjacentEdge( pID, List( edge ) ) shouldBe false
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        game2.playerHasAdjacentEdge( pID, List( game2.gameField.edgeList.head ) ) shouldBe true
      }
      "playerHasAdjacentVertex" in {
        val vertex = game.gameField.vertexList.head
        game.playerHasAdjacentVertex( pID, List( vertex ) ) shouldBe false
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.playerHasAdjacentVertex( pID, List( game2.gameField.vertexList.head ) ) shouldBe true
      }
      "roadBuildable" in {
        val edge = game.gameField.findEdge( 44 )
        edge shouldNot be( None )
        game.roadBuildable( edge.get, pID ) shouldBe false
        val vertex = game.gameField.findVertex( 27 )
        vertex shouldNot be( None )
        val game2 = game.setGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.roadBuildable( edge.get, pID ) shouldBe true
        val game3 = game2.setGameField( game2.gameField.update( edge.get.setRoad( Some( Road( pID ) ) ) ) )
        val edge2 = game3.gameField.findEdge( 45 )
        edge2 shouldNot be( None )
        game3.roadBuildable( edge2.get, pID ) shouldBe true
        val edge3 = game3.gameField.findEdge( 42 )
        edge3 shouldNot be( None )
        game3.roadBuildable( edge3.get, pID ) shouldBe false
      }
      "roadLength" in {
        val edges = game.gameField.adjacentEdges( game.gameField.findHex( 19 ).get )
        game.getLongestRoadLength( pID ) shouldBe 0
        val game2 = game.setGameField( edges.red( game.gameField,
          ( gf:GameField, e:Edge ) => gf.update( e.setRoad( Some( Road( pID ) ) ) ) )
        )
        game2.getLongestRoadLength( pID ) shouldBe 6
      }
      "checkHandCardsInOrder" in {
        val game2 = game.drawResourceCards( pID, Wood, game.maxHandCards + 1 )._1
        game2.checkHandCardsInOrder() shouldBe Some( game2.player )
        val pID1 = game.getPlayerID( 1 )
        pID1 shouldNot be( None )
        val game3 = game.drawResourceCards( pID1.get, Wood, game.maxHandCards + 1 )._1
        game3.checkHandCardsInOrder( game3.player, List.empty ) shouldBe Some( game3.player( pID1.get ) )
      }
      "getBuildableRoadSpotsForSettlement" in {
        val vID = 1
        game.getBuildableRoadSpotsForSettlement( vID ).map( _.id ) shouldBe empty
        val vertex = game.gameField.findVertex( vID )
        vertex shouldNot be( None )
        val game2 = game.setGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.getBuildableRoadSpotsForSettlement( vID ).map( _.id ) should contain theSameElementsAs List( 1, 3 )
      }
      "getBankTradeFactor" in {
        val vertex = game.gameField.vertexList.find( v => v.port.isEmpty )
        vertex shouldNot be( None )
        val game2 = game.setGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game2.getBankTradeFactor( pID, Wood ) shouldBe game.defaultBankTradeFactor
        val vertex2 = game2.gameField.vertexList.find( v => v.port.isDefined && v.port.get.specific.isEmpty )
        vertex2 shouldNot be( None )
        val game3 = game2.setGameField( game2.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game3.getBankTradeFactor( pID, Wood ) shouldBe game.unspecifiedPortFactor
        val vertex3 = game3.gameField.vertexList.find( v => v.port.isDefined && v.port.get.specific.isDefined && v.port.get.specific.get == Wood )
        vertex3 shouldNot be( None )
        val game4 = game3.setGameField( game3.gameField.update( vertex3.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        game4.getBankTradeFactor( pID, Wood ) shouldBe game.specifiedPortFactor
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
}
