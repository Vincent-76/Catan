package de.htwg.se.catan.aview.tui.impl.player

import de.htwg.se.catan.aview.tui.{ PlayerDisplay, TUI }
import de.htwg.se.catan.model.Cards._
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.{ Cards, Game, Resources }
import de.htwg.se.catan.util._

case class ClassicPlayerDisplayImpl( player:ClassicPlayerImpl ) extends PlayerDisplay {

  def buildPlayerDisplay( game:Game ):String = {
    val nameLength = game.players.map( _._2.idName.length ).max
    TUI.displayName( player, nameLength ) +
      " Resources[" + player.resources.amount.toLength( 2 ) + "]" +
      " Points[" + game.getPlayerDisplayVictoryPoints( player.id ).toLength( 2 ) + "]" +
      " DevCards[" + player.devCards.size.toLength( 2 ) + "]" +
      " UsedDevCards[" + Cards.devCards.flatMap( c => (0 until player.usedDevCards( c )).map( _ => c ) ).map( _.title ).mkString( "|" ) + "] " +
      game.getPlayerBonusCards( player.id ).mkString( " " )
  }

  def buildTurnPlayerDisplay( game:Game ):String = {
    val resourceNameLength = Resources.get.map( _.title.length ).max
    TUI.displayName( player ) +
      "\nVictory Points: " + game.getPlayerVictoryPoints( player.id ) +
      "\nResources:" + player.resources.amount + "\n" +
      player.resources.sort.map( d => "  " + d._1.title.toLength( resourceNameLength ) + "  " + d._2 ).mkString( "\n" ) +
      "\nDevelopment Cards: [" + player.devCards.map( _.title ).mkString( "|" ) + "]" +
      "\nUsed Dev Cards:    [" + player.usedDevCards.map( _.title ).mkString( "|" ) + "]" +
      game.getPlayerBonusCards( player.id ).map( c => "\n" + c.title ).mkString
  }
}
