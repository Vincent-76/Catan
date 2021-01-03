package de.htwg.se.munchkin.ui

import de.htwg.se.munchkin.model._
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
case class GameDisplay( game:Game ) {

  def buildBeginPhase:String = {
    buildOtherPlayers( true ) + appendTreasureDisplay + appendDoorDisplay
  }

  def buildDoorPhase:String = {
    buildOtherPlayers() + appendTreasureDisplay + appendDoorDisplay + appendTurnPlayer
  }

  def buildTurnPlayer( s:String ):String = {
    ???
  }

  def buildOtherPlayers( all:Boolean = false ):String = game.players.red( "", ( s:String, player:Player ) => {
    s + ( if ( !player.onTurn( game.turn ) ) player.display + "\n\n" else if ( all ) "->\t" + player.display.tab + "\n\n" else "" )
  } ) + "\n"

  def appendTreasureDisplay:String = {
    "TreasureCards[" + game.treasureQueue.size + "]\n->\t" + ( if( game.treasureStack.nonEmpty ) game.treasureStack.last.display().tab else "" ) + "\n\n"
  }

  def appendDoorDisplay:String = appendDoorDisplay()

  def appendDoorDisplay( full:Boolean = false ):String = {
    "DoorCards[" + game.doorQueue.size + "]\n->\t" + ( if( game.doorStack.nonEmpty ) game.doorStack.last.display( full ).tab else "" ) + "\n\n"
  }

  def appendTurnPlayer:String = {
    game.turnPlayer.display( true ) + "\n\n"
  }

}
