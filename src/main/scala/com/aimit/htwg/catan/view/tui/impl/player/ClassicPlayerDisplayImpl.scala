package com.aimit.htwg.catan.view.tui.impl.player

import com.aimit.htwg.catan.view.tui.{ PlayerDisplay, TUI }
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.{ Card, DevelopmentCard, Game, Resource }
import com.aimit.htwg.catan.util._

case class ClassicPlayerDisplayImpl( player:ClassicPlayerImpl ) extends PlayerDisplay {

  def buildPlayerDisplay( game:Game ):String = {
    val nameLength = game.players.map( _._2.idName.length ).max
    TUI.displayName( player, nameLength ) +
      " Resources[" + player.resources.amount.toLength( 2 ) + "]" +
      " Points[" + game.getPlayerDisplayVictoryPoints( player.id ).toLength( 2 ) + "]" +
      " DevCards[" + player.devCards.size.toLength( 2 ) + "]" +
      " UsedDevCards[" + DevelopmentCard.impls.flatMap( c => (0 until player.usedDevCards( c )).map( _ => c ) ).map( _.title ).mkString( "|" ) + "] " +
      game.getPlayerBonusCards( player.id ).mkString( " " )
  }

  def buildTurnPlayerDisplay( game:Game ):String = {
    val resourceNameLength = Resource.impls.map( _.title.length ).max
    TUI.displayName( player ) +
      "\nVictory Points: " + game.getPlayerVictoryPoints( player.id ) +
      "\nResources:" + player.resources.amount + "\n" +
      player.resources.sort.map( d => "  " + d._1.title.toLength( resourceNameLength ) + "  " + d._2 ).mkString( "\n" ) +
      "\nDevelopment Cards: [" + player.devCards.map( _.title ).mkString( "|" ) + "]" +
      "\nUsed Dev Cards:    [" + player.usedDevCards.map( _.title ).mkString( "|" ) + "]" +
      game.getPlayerBonusCards( player.id ).map( c => "\n" + c.title ).mkString
  }
}
