package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util.Observable

import scala.util.{Failure, Success}

/**
 * @author Vincent76;
 */

class Controller( test:Boolean = false/*, debug:Boolean = false*/ ) extends Observable {
  var running:Boolean = true
  var game:Game = Game( InitState(), test )
  private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil

  def onTurn:PlayerID = game.turn.playerID

  def player:Player = player()

  def player( pID:PlayerID = onTurn ):Player = game.players( pID )

  def hasUndo:Boolean = undoStack.nonEmpty

  def hasRedo:Boolean = redoStack.nonEmpty

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => newGame.getPlayerVictoryPoints( p.id ) >= Game.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone( newGame:Game, command:Command, newRedoStack:List[Command], info:Option[Info] ):Unit = {
    game = newGame
    undoStack = command :: undoStack
    redoStack = newRedoStack
    checkWinner( game ) match {
      case None =>
        update( info )
        //if ( info.isDefined ) info( info.get )
      case Some( pID ) =>
        game = game.copy( winner = Some( pID ) )
        exit( Some( GameEndInfo( pID ) ) )
    }
  }

  def action( command:Option[Command] ):Unit = {
    if( command.isEmpty )
      error( WrongState )
    else command.get.doStep( this.game ) match {
      case Success( (game, info) ) => actionDone( game, command.get, Nil, info )
      case Failure( t ) =>
        error( t )
    }
  }

  def undoAction():Unit = undoStack match {
    case Nil => error( NothingToUndo )
    case head :: stack =>
      this.game = head.undoStep( this.game )
      undoStack = stack
      redoStack = head :: redoStack
      update()
  }

  def redoAction():Unit = redoStack match {
    case Nil => error( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this.game ).get
      actionDone( game, head, stack, info )
  }

  def exit( info:Option[Info] = None ):Unit = {
    running = false
    update( info )
  }


  def initPlayers():Unit = action( game.state.initPlayers() )

  def addPlayer( playerColor:PlayerColor, name:String ):Unit = action( game.state.addPlayer( playerColor, name ) )

  def setInitBeginnerState():Unit = action( game.state.setInitBeginnerState() )

  def diceOutBeginner():Unit = action( game.state.diceOutBeginner() )

  def setBeginner():Unit = action( game.state.setBeginner() )

  def buildInitSettlement( vID:Int ):Unit = action( game.state. buildInitSettlement( vID ))

  def buildInitRoad( eID:Int ):Unit = action( game.state.buildInitRoad( eID ) )

  def startTurn():Unit = action( game.state.startTurn() )

  def rollTheDices():Unit = action( game.state.rollTheDices() )

  def useDevCard( devCard:DevelopmentCard ):Unit = action( game.state.useDevCard( devCard ) )

  def dropResourceCardsToRobber( cards:ResourceCards ):Unit = action( game.state.dropResourceCardsToRobber( cards ) )

  def placeRobber( hID:Int ):Unit = action( game.state.placeRobber( hID ) )

  def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = action( game.state.robberStealFromPlayer( stealPlayerID ) )

  def setBuildState( structure:StructurePlacement ):Unit = action( game.state.setBuildState( structure ) )

  def build( id:Int ):Unit = action( game.state.build( id ) )

  def bankTrade( give:ResourceCards, get:ResourceCards ):Unit = action( game.state.bankTrade( give, get ) )

  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = action( game.state.setPlayerTradeState( give, get ) )

  def playerTradeDecision( decision:Boolean ):Unit = action( game.state.playerTradeDecision( decision ) )

  def abortPlayerTrade():Unit = action( game.state.abortPlayerTrade() )

  def playerTrade( tradePlayerID:PlayerID ):Unit = action( game.state.playerTrade( tradePlayerID) )

  def buyDevCard():Unit = action( game.state.buyDevCard() )

  def yearOfPlentyAction( resources:ResourceCards ):Unit = action( game.state.yearOfPlentyAction( resources ) )

  def devBuildRoad( eID:Int ):Unit = action( game.state.devBuildRoad( eID ) )

  def monopolyAction( r:Resource ):Unit = action( game.state.monopolyAction( r ) )

  def endTurn():Unit = action( game.state.endTurn() )
}
