package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.model.PlayerProperties.Properties

import scala.util.{ Failure, Success, Try }
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
case class Player( id:Int,
                   name:String,
                   gender:Gender,
                   hand:Vector[Card] = Vector(),
                   equipped:Vector[Card] = Vector(),
                   level:Int = 1 ) {

  val properties:Properties = PlayerProperties.checkCards( equipped )

  def totalGain:Int = equipped.map( _.getGain ).sum

  def playerProperties:Properties = PlayerProperties.checkCards( equipped )

  def combatLevel:Int = this.level + totalGain

  def onTurn( turn:Int ):Boolean = id == turn

  def changeLevel( level:Int ):Player = copy( level = this.level + level )

  def addHandCard( card:Card ):Player = copy( hand = hand :+ card )

  def equipCard( card:Card ):Try[Player] = {
    if ( !hand.contains( card ) )
      return Failure( new NotFoundException )
    if ( !card.equippable )
      return Failure( new UnequippableException )
    for ( (property, requirement) <- card.getPropertyChanges ) {
      if ( !properties.contains( property ) || ( property.min.isDefined && ( properties( property ) + requirement ) < property.min.get ) )
        return Failure( new PlayerPropertyRequirementException( property, 0 - requirement, properties( property ) ) )
    }
    Success( copy( equipped = equipped :+ card, hand = hand.filter( _ != card ) ) )
  }

  def display:String = display()

  def display( full:Boolean = false ):String = "<" + id + ">" + ( if ( full ) toFullString else toShortString )

  private def toShortString:String = name + "[" + gender.short + "][" + level + "][" + totalGain.toDisplay + "]" +
    "[" + hand.map( _.symbol ).mkString( "|" ) + "]" +
    equipped.red( "", ( s:String, card:Card ) => s + "\n\t" + card.display )

  private def toFullString:String = name + "[" + gender.short + "][" + level + "][" + totalGain.toDisplay + "]" +
    "\nEquipped:" + equipped.red( "", ( s:String, card:Card ) => s + "\n\t" + card.display ) +
    "\nHand:" + hand.red( "", ( s:String, card:Card ) => s + "\n\t" + card.display )
}


sealed abstract class Gender( val short:String )

object Gender {
  def chooseList:Iterable[Gender] = Iterable( Male, Female )

  def stringList:String = chooseList.map( _.short ) mkString( "/" )

  def regexToken:String = chooseList.red( "", ( s:String, gender:Gender ) => s + gender.short.toLowerCase + gender.short.toUpperCase )

  def fromShort( short:String ):Gender = chooseList.find( _.short.toLowerCase.equals( short.toLowerCase ) ).getOrElse( Male )
}

object Male extends Gender( "M" )

object Female extends Gender( "F" )

object Transgender extends Gender( "T" )