package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.GameField._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.ui.tui.{ TUI, UI }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */

class Controller {
  var running:Boolean = true
  val ui:UI = new TUI( this )
  var game:Game = Game()

  ui.start()

  type ControllerAnswer = Option[Throwable]

  def gameField:GameField = game.gameField

  def turn:Turn = game.turn

  def onTurn:Int = game.turn.playerID

  def nextTurn( pID:Int = onTurn ):Int = if ( pID < game.players.size - 1 ) pID + 1 else 0

  def previousTurn( pID:Int = onTurn ):Int = if ( pID > 0 ) pID - 1 else game.players.size - 1

  def player:Player = player()

  def player( id:Int = onTurn ):Player = game.players( id )

  def nextPlayer( p:Player = player ):Player = if ( p.id < game.players.size - 1 ) player( p.id + 1 ) else player( 0 )

  def previousPlayer( p:Player = player ):Player = if ( p.id > 0 ) player( p.id - 1 ) else player( game.players.size - 1 )

  def exit( ):ControllerAnswer = {
    running = false
    Option.empty
  }

  private def checkWinner( newGame:Game ):Game = {
    newGame.players.find( p => p.getVictoryPoints( newGame ) >= Game.requiredVictoryPoints ) match {
      case Some( p ) =>
        running = false
        newGame.copy( winner = Some( p.id ) )
      case _ => newGame
    }
  }

  def action( newGame:Game ):ControllerAnswer = {
    this.game = checkWinner( newGame )
    Option.empty
  }

  def action( newGame:Try[Game] ):ControllerAnswer = newGame match {
    case Success( newGame ) =>
      action( newGame )
    case Failure( f:ControllerError ) => Some( f )
    case _ => Some( Fail )
  }

  def rollDice( ):Int = Random.nextInt( 6 ) + 1

  private def setPhase( phase:Phase ):ControllerAnswer = action( game.setPhase( phase ) )


  def initPlayerPhase( ):ControllerAnswer = game.phase match {
    case InitPhase => action( game.setPhase( InitPlayerPhase ) )
    case _ => Some( WrongPhase )
  }

  def addPlayer( color:String, name:String ):ControllerAnswer = game.phase match {
    case InitPlayerPhase =>
      val playerColor = Player.colorOf( color )
      if ( playerColor.isEmpty )
        return Some( InvalidPlayerColor )
      val player = Player( game.players.size, playerColor.get, name )
      if ( game.players.size + 1 >= Game.maxPlayers )
        action( game.copy( phase = InitBeginnerPhase, players = game.players :+ player ) )
      else
        action( game.copy( players = game.players :+ player ) )
    case _ => Some( WrongPhase )
  }

  def setInitBeginnerPhase( ):ControllerAnswer = game.phase match {
    case InitPlayerPhase =>
      if ( game.players.size < Game.minPlayers )
        return Some( NotEnoughPlayers )
      action( game.copy( phase = InitBeginnerPhase ) )
    case _ => Some( WrongPhase )
  }

  def setInitBuildPhase( beginnerID:Int ):ControllerAnswer = game.phase match {
    case InitBeginnerPhase => action( game.copy( phase = InitBuildSettlementPhase, turn = Turn( beginnerID ) ) )
    case _ => Some( WrongPhase )
  }

  def setInitBuildSettlement( playerID:Int, vID:Int ):ControllerAnswer = game.phase match {
    case InitBuildSettlementPhase =>
      val newGame = game.build( playerID, Settlement, vID, anywhere = true )
      if ( newGame.isFailure )
        return newGame.failureOption
      if ( newGame.get.settlementAmount( onTurn ) == 2 ) {
        val vertex = newGame.get.gameField.findVertex( vID )
        if ( vertex.isDefined ) {
          val resources = vertex.get.hexes.red( Map.empty.asInstanceOf[ResourceCards], ( c:ResourceCards, h:Hex ) => {
            h.area.f match {
              case r:Resource => c.add( r, 1 )
              case _ => c
            }
          } )
          return action( newGame.get.drawResourceCards( playerID, resources ).copy( phase = InitBuildRoadPhase( vID ) ) )
        }
      }
      action( newGame.get.copy( phase = InitBuildRoadPhase( vID ) ) )
    case _ => Some( WrongPhase )
  }

  def setInitBuildRoad( eID:Int ):ControllerAnswer = game.phase match {
    case p:InitBuildRoadPhase =>
      val vertex = gameField.findVertex( p.settlementVID )
      if ( vertex.isEmpty || !gameField.adjacentEdges( vertex.get ).exists( _.id == eID ) )
        return Some( InvalidPlacementPoint )
      val newGame = game.build( onTurn, Road, eID )
      if ( newGame.isFailure )
        return newGame.failureOption
      val (nTurn, nPhase) = game.settlementAmount( onTurn ) match {
        case 1 => game.settlementAmount( nextTurn() ) match {
          case 0 => (nextTurn(), InitBuildSettlementPhase)
          case _ => (onTurn, InitBuildSettlementPhase)
        }
        case 2 => game.settlementAmount( previousTurn() ) match {
          case 1 => (previousTurn(), InitBuildSettlementPhase)
          case _ => (onTurn, NextPlayerPhase)
        }
      }
      action( newGame.get.copy( phase = nPhase, turn = Turn( nTurn ) ) )
    case _ => Some( WrongPhase )
  }

  def setNextPlayerPhase( ):ControllerAnswer = game.phase match {
    case ActionPhase => action( game.copy( phase = NextPlayerPhase, turn = Turn( nextTurn() ) ) )
    case _ => Some( WrongPhase )
  }

  def setTurnStartPhase( ):ControllerAnswer = game.phase match {
    case NextPlayerPhase => setPhase( TurnStartPhase )
    case _ => Some( WrongPhase )
  }

  def setDicePhase( ):ControllerAnswer = game.phase match {
    case TurnStartPhase => setPhase( DicePhase )
    case _ => Some( WrongPhase )
  }

  def rollDices( ):Try[(Int, Int)] = game.phase match {
    case DicePhase =>
      val d = (rollDice(), rollDice())
      val number = Numbers.of( d._1 + d._2 )
      number match {
        case Seven => if ( game.players.exists( _.resources.amount > 7 ) )
          action( game.setPhase( DropResourceCardPhase( List.empty ) ) )
        else
          action( game.setPhase( RobberPlacePhase( ActionPhase ) ) )
        case _ => action( game.gathererPhase( number, d ) )
      }
      Success( d )
    case _ => Failure( WrongPhase )
  }

  def checkHandCardsInOrder( p:Player = player ):Option[Player] = game.phase match {
    case DropResourceCardPhase( dropped ) =>
      if ( p.resources.amount > 7 && !dropped.contains( p.id ) )
        Some( p )
      else {
        val next = nextPlayer( p )
        if ( next.id != onTurn )
          checkHandCardsInOrder( next )
        else {
          action( game.setPhase( RobberPlacePhase( ActionPhase ) ) )
          Option.empty
        }
      }
  }

  def dropResourceCardsToRobber( playerID:Int, cards:ResourceCards ):ControllerAnswer = game.phase match {
    case d:DropResourceCardPhase =>
      if ( cards.amount != ( player( playerID ).resources.amount / 2 ) )
        return Some( WrongResourceAmount( cards.amount ) )
      game.dropResourceCards( playerID, cards ) match {
        case Success( newGame ) => action( newGame.setPhase( d.copy( dropped = d.dropped :+ playerID ) ) )
        case Failure( e ) => Some( e )
      }
    case _ => Some( WrongPhase )
  }

  def placeRobber( hID:Int ):Try[Option[Option[Resource]]] = game.phase match {
    case r:RobberPlacePhase => game.placeRobber( hID, r.nextPhase ) match {
      case Success( d ) =>
        action( d._2 )
        Success( d._1 )
      case Failure( e ) => Failure( e )
    }
    case _ => Failure( WrongPhase )
  }

  def robberStealFromPlayer( stealPlayerID:Int ):Try[Option[Resource]] = game.phase match {
    case r:RobberStealPhase => game.robberSteal( onTurn, stealPlayerID, r.nextPhase ) match {
      case Success( d ) =>
        action( d._2 )
        Success( d._1 )
      case Failure( e ) => Failure( e )
    }
    case _ => Failure( WrongPhase )
  }

  def setActionPhase( ):ControllerAnswer = game.phase match {
    case _:GatherPhase => setPhase( ActionPhase )
    case _ => Some( WrongPhase )
  }

  def setBuildPhase( structure:StructurePlacement ):ControllerAnswer = game.phase match {
    case ActionPhase =>
      if ( !player.hasStructure( structure ) )
        return Some( InsufficientStructures( structure ) )
      if ( game.getBuildableIDsForPlayer( onTurn, structure ).isEmpty )
        return Some( NoPlacementPoints )
      game.dropResourceCards( onTurn, structure.resources ) match {
        case Success( newGame ) => action( newGame.setPhase( BuildPhase( structure ) ) )
        case Failure( e:ControllerError ) => Some( e )
        case _ => Some( Fail )
      }
    case _ => Some( WrongPhase )
  }

  def build( structure:StructurePlacement, id:Int ):ControllerAnswer = game.phase match {
    case _:BuildPhase => game.build( onTurn, structure, id ) match {
      case Failure( e ) => Some( e )
      case Success( newGame ) => action( newGame.setPhase( ActionPhase ) )
    }
    case _ => Some( WrongPhase )
  }

  def bankTrade( give:(Resource, Int), get:(Resource, Int) ):Try[((Resource, Int), (Resource, Int))] = game.phase match {
    case ActionPhase =>
      if ( give._1 == get._1 )
        return Failure( InvalidTradeResources( give._1, get._1 ) )
      val factor = game.getBankTradeFactor( onTurn, give._1 )
      val amount = give._2 / factor
      if ( amount < 0 || amount < get._2 )
        return Failure( InsufficientResources )
      if( game.resourceStack.getOrElse( get._1, 0 ) < get._2 )
        return Failure( InsufficientBankResources( get._1 ) )
      val newGame = game.drawResourceCards( onTurn, get._1, get._2 ).dropResourceCards( onTurn, give._1, get._2 * factor )
      if( newGame.isFailure )
        return newGame.rethrow
      action( newGame.get )
      Success( (give._1, get._2 * factor), (get._1, get._2) )
    case _ => Failure( WrongPhase )
  }

  def setPlayerTradePhase( give:ResourceCards, get:ResourceCards ):ControllerAnswer = game.phase match {
    case ActionPhase =>
      if ( !player.resources.has( give ) )
        return Some( InsufficientResources )
      val decisions = game.players.filter( p => p != player && !p.resources.has( get ) ).map( p => (p.id, false) ).toMap
      action( game.copy( phase = PlayerTradePhase( give, get, decisions ) ) )
    case _ => Some( WrongPhase )
  }

  def checkTradePlayerInOrder( playerTradePhase:PlayerTradePhase, p:Player = nextPlayer() ):Option[Player] = {
    if ( p == player )
      return Option.empty
    if ( playerTradePhase.decisions.contains( p.id ) )
      return checkTradePlayerInOrder( playerTradePhase, nextPlayer( p ) )
    Some( p )
  }

  def setPlayerTradeDecision( p:Player, decision:Boolean ):ControllerAnswer = game.phase match {
    case t:PlayerTradePhase => action( game.copy( phase = t.copy( decisions = t.decisions.updated( p.id, decision ) ) ) )
    case _ => Some( WrongPhase )
  }

  def abortPlayerTrade( ):ControllerAnswer = game.phase match {
    case _:PlayerTradePhase => setPhase( ActionPhase )
    case _ => Some( WrongPhase )
  }

  def playerTrade( tradePlayerID:Int ):ControllerAnswer = game.phase match {
    case t:PlayerTradePhase =>
      val newPlayer = player.trade( t.get, t.give )
      if ( newPlayer.isFailure )
        return Some( InsufficientResources )
      val tradePlayer = player( tradePlayerID ).trade( t.give, t.get )
      if ( tradePlayer.isFailure )
        return Some( TradePlayerInsufficientResources )
      val nPlayers = game.players.updated( newPlayer.get.id, newPlayer.get ).updated( tradePlayer.get.id, tradePlayer.get )
      action( game.copy( players = nPlayers, phase = ActionPhase ) )
    case _ => Some( WrongPhase )
  }

  def buyDevCard( ):ControllerAnswer = game.phase match {
    case ActionPhase => action( game.drawDevCard( onTurn ) )
    case _ => Some( WrongPhase )
  }

  def useDevCard( devCard:DevelopmentCard ):ControllerAnswer = game.phase match {
    case ActionPhase | TurnStartPhase =>
      if( game.turn.usedDevCard )
        return Some( AlreadyUsedDevCardInTurn )
      val newPlayer = player.useDevCard( devCard )
      if ( newPlayer.isFailure )
        return newPlayer.failureOption
      val nextPhase = devCard match {
        case KnightCard => RobberPlacePhase( game.phase )
        case YearOfPlentyCard => DevYearOfPlentyPhase( game.phase )
        case RoadBuildingCard => DevRoadBuildingPhase( game.phase )
        case MonopolyCard => DevMonopolyPhase( game.phase )
        case _ => game.phase
      }
      val newBonusCards = if ( devCard == KnightCard ) {
        val amount = newPlayer.get.usedDevCards.count( _ == KnightCard )
        val largestArmy = game.bonusCards( LargestArmyCard )
        if ( amount >= LargestArmyCard.required && ( largestArmy.isEmpty || amount > largestArmy.get._2 ) )
          game.bonusCards.updated( LargestArmyCard, Some( newPlayer.get.id, amount ) )
        else game.bonusCards
      }
      else game.bonusCards
      action( game.copy(
        phase = nextPhase,
        turn = turn.copy( usedDevCard = true ),
        players = game.players.updated( newPlayer.get.id, newPlayer.get ),
        bonusCards = newBonusCards,
      ) )
  }

  def yearOfPlentyAction( resources:ResourceCards ):Try[ResourceCards] = game.phase match {
    case DevYearOfPlentyPhase( nextPhase ) =>
      if ( resources.amount != 2 )
        return Failure( WrongResourceAmount( resources.amount ) )
      action( game.drawResourceCards( onTurn, resources ).setPhase( nextPhase ) )
      Success( resources )
    case _ => Failure( WrongPhase )
  }

  def devBuildRoad( eID:Int ):ControllerAnswer = game.phase match {
    case p:DevRoadBuildingPhase =>
      if( !player.hasStructure( Road ) ) {
        action( game.setPhase( p.nextPhase ) )
        return Some( InsufficientStructures( Road ) )
      }
      if( game.getBuildableIDsForPlayer( onTurn, Road ).isEmpty ) {
        action( game.setPhase( p.nextPhase ) )
        return Some( NoPlacementPoints )
      }
      game.build( onTurn, Road, eID ) match {
      case Success( newGame ) =>
        val nextPhase = if( p.roads == 0 )
          p.copy( roads = p.roads + 1 )
        else p.nextPhase
        action( newGame.setPhase( nextPhase ) )
      case Failure( e ) => Some( e )
    }
    case _ => Some( WrongPhase )
  }

  def monopolyAction( r:Resource ):Try[Int] = game.phase match {
    case DevMonopolyPhase( nextPhase ) =>
      val newData = game.players.red( (game.players, 0), ( data:(Vector[Player], Int), p:Player ) => {
        if ( p.id != onTurn ) {
          val amount = p.resources.getOrElse( r, 0 )
          (data._1.updated( p.id, p.removeResourceCard( r, amount ).get ), data._2 + amount)
        } else data
      } )
      val newPlayers = if ( newData._2 > 0 )
        newData._1.updated( onTurn, player.addResourceCard( r, newData._2 ) )
      else newData._1
      action( game.copy(
        phase = nextPhase,
        players = newPlayers
      ) )
      Success( newData._2 )
    case _ => Failure( WrongPhase )
  }

}
