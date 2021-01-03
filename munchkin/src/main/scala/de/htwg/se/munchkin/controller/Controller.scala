package de.htwg.se.munchkin.controller

import de.htwg.se.munchkin.model.{ BeginPhase, Card, CurseCard, CursePhase, DoorCard, DoorCards, DoorPhase, FightPhase, Game, Gender, MonsterCard, Phase, Player, PlayerPhase, SecondDoorPhase, TreasureCard, TreasureCards, TurnPhase }
import de.htwg.se.munchkin.ui.TUI
import de.htwg.se.munchkin.util._

import scala.util.Random

/**
 * @author Vincent76;
 */
class Controller( ) {
  var running:Boolean = true
  var games:List[Game] = List( Game( DoorCards.random, TreasureCards.random ) )
  var undone:List[Game] = List()
  val ui:TUI = new TUI( this )

  def game:Game = games.head

  def player:Player = player()

  def player( id:Int = game.turn ):Player = game.players( id )

  def undo( ):Boolean = {
    if ( games.size <= 1 )
      return false
    undone = games.head :: undone
    games = games.tail
    true
  }

  def redo( ):Boolean = {
    if( undone.isEmpty )
      return false
    games = undone.head :: games
    undone = undone.tail
    true
  }

  def exit( ):Unit = {
    running = false
  }

  private def action( newGame:Game ):Unit = {
    if ( games.size > 10 )
      games = newGame :: games.init
    else
      games = newGame :: games
    undone = List()
  }

  def addPlayer( name:String, gender:Gender ):Unit = {
    val (doorCards, newGame) = game.drawDoor( 3 )
    val (treasureCards, newGame2) = newGame.drawTreasure( 2 )
    val player = Player( game.players.size, name, gender, Vector() ++ doorCards ++ treasureCards )
    action( newGame2.copy( players = game.players :+ player ) )
  }

  def setTurn( turn:Int ):Unit = action( game.copy( turn = turn ) )

  def updatePlayer( pID:Int, player:Player ):Unit = action( game.updatePlayer( pID, player ) )

  def dropCard( card:Card ):Unit = dropCards( card :: Nil )

  def dropCards( cards:List[Card] ):Unit = action( game.dropCards( cards ) )

  def setPlayerPhase( ):Unit = action( game.copy( phase = PlayerPhase ) )

  def setTurnPhase( ):Boolean = {
    if ( game.players.size < 2 )
      return false
    action( game.copy( phase = TurnPhase ) )
    true
  }

  def startGame( turn:Int ):Unit = {
    action( game.copy( turn = turn, phase = BeginPhase ) )
  }

  def setBeginPhase( ):Unit = action( game.setPhase( BeginPhase ) )

  def setDoorPhase( ):Unit = action( game.setPhase( DoorPhase ) )

  def drawDoorCard( ):Option[DoorCard] = {
    val (cardOption, nGame) = game.drawDoor
    if ( cardOption.isEmpty )
      exit()
    else {
      val nGame2 = cardOption.get match {
        case card:MonsterCard => nGame.dropCard( card ).setPhase( FightPhase )
        case card:CurseCard => nGame.dropCard( card ).setPhase( CursePhase )
        case _ => nGame.updatePlayer( nGame.turn, nGame.turnPlayer.addHandCard( cardOption.get ) ).setPhase( SecondDoorPhase )
      }
      action( nGame2 )
    }
    cardOption
  }

  def DoorEncounter( nGame:Game, card:DoorCard, phase:Phase ):Game = {
    nGame.dropCard( card ).setPhase( phase )
  }

  def rollDice:Int = Random.nextInt( 6 ) + 1

  def viewCard( pID:Int, cID:Int ):Option[Card] = {
    if( game.doorStack.nonEmpty && game.doorStack.head.id == cID )
      return Some( game.doorStack.head )
    if( game.treasureStack.nonEmpty && game.treasureStack.head.id == cID )
      return Some( game.treasureStack.head )
    game.players.foreach( p => {
      val equippedCard = p.equipped.find( _.id == cID )
      if( equippedCard.isDefined ) return equippedCard
      if( p.id == pID ) {
        val handCard = p.hand.find( _.id == cID )
        if( handCard.isDefined ) return handCard
      }
    } )
    Option.empty
  }
}
