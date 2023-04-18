package de.htwg.se.catan.aview.tui

import de.htwg.se.catan.model.{ Game, PlayerID }

/**
 * @author Vincent76;
 */
trait TUIState:

  def buildPlayerDisplay( game:Game, onTurn:Option[PlayerID] = None ):String =
    val playersDisplay = game.players.values.filter( onTurn.isEmpty || _.id != onTurn.get ).toSeq.sortBy( _.id.id ).map( p =>
      PlayerDisplay.get( p ).buildPlayerDisplay( game )
    ).mkString( "\n" ) + "\n\n"
    if onTurn.isDefined then
      playersDisplay + PlayerDisplay.get( game.players( onTurn.get ) ).buildTurnPlayerDisplay( game )
    else playersDisplay
  

  def createGameDisplay:Option[String] = None

  def getActionInfo:String

  def inputPattern:Option[String] = None

  def action( commandInput: CommandInput ):Unit
