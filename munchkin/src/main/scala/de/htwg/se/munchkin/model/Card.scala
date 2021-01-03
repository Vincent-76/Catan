package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.model.PlayerProperties.Properties
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */

abstract class Card( val id:Int,
                     val title:String,
                     val text:String,
                     val equippable:Boolean = false,
                     val appendable:Boolean = false,
                     private val gain:Int = 0,
                     private val propertyChanges:Properties = Map(),
                     val cardAction:Option[Action] = Option.empty,
                     val appendedCards:Vector[Card] = Vector()
                   ) {

  def symbol:String

  def display:String = display()

  def display( full:Boolean = false ):String = "<" + id + ">" + ( if ( full ) toFullString else toShortString )

  protected def toShortString:String

  protected def toFullString:String

  def getGain:Int = gain + appendedCards.map( _.getGain ).sum

  def getPropertyChanges:Properties = {
    PlayerProperties.checkCards( appendedCards, propertyChanges )
  }

  implicit class PropertyChangesMap( val propertyChanges:Properties ) {
    def toDisplay:String = propertyChanges.keySet.toSeq.sortBy( _.name ).red( "", ( s:String, property:PlayerProperty ) => {
      val value = propertyChanges.get( property )
      if ( value.isDefined && value.get != 0 ) s + "[" + property.name + value.get.toDisplay + "]" else s
    } )
  }

}

class CardSkill( val title:String, val text:String, val action:Action )
