package de.htwg.se.catan.model.impl.slick

import slick.jdbc.MySQLProfile._
import slick.jdbc.MySQLProfile.api._

/**
 * @author Vincent76
 */

class SlickClassicGame( tag:Tag ) extends Table[(Int, String, String, Int, String, String, String, String, String, String, String, String, Int)]( tag, "ClassicGame" ):
  def id = column[Int]( "id", O.PrimaryKey, O.AutoInc )
  def gameField = column[String]( "gameField" )
  def turn = column[String]( "turn" )
  def seed = column[Int]( "seed" )
  def playerFactoryClass = column[String]( "playerFactoryClass" )
  def availablePlacements = column[String]( "availablePlacements" )
  def state = column[String]( "state" )
  def resourceStack = column[String]( "resourceStack" )
  def developmentCards = column[String]( "developmentCards" )
  def players = column[String]( "players" )
  def bonusCards = column[String]( "bonusCards" )
  def winner = column[String]( "winner" )
  def round = column[Int]( "round" )

  def * = (id, gameField, turn, seed, playerFactoryClass, availablePlacements, state, resourceStack, developmentCards, players, bonusCards, winner, round)

