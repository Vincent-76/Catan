package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.model.PlayerProperties.Properties
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
class TreasureCard( id:Int,
                    title:String,
                    text:String,
                    equippable:Boolean = false,
                    appendable:Boolean = false,
                    gain:Int = 0,
                    propertyChanges:Properties = Map.empty,
                    cardAction:Option[Action] = Option.empty
                  ) extends Card( id, title, text, equippable, appendable, gain, propertyChanges, cardAction ) {

  override def symbol:String = "T"

  override protected def toShortString:String = title + gain.toDisplay + propertyChanges.toDisplay

  override protected def toFullString:String = toShortString + "\n" + text
}

class ItemCard( id:Int,
                title:String,
                text:String,
                gain:Int,
                propertyChanges:Properties = Map.empty
              ) extends TreasureCard( id, title, text, true, gain = gain, propertyChanges = propertyChanges ) {

  override protected def toShortString:String = title + gain.toFullDisplay + propertyChanges.toDisplay

  override protected def toFullString:String = toShortString + "\n" + text
}

class ItemEnhancerCard( id:Int,
                        title:String,
                        text:String,
                        gain:Int
                      ) extends TreasureCard( id, title, text, appendable = true, gain = gain ) {

  override protected def toShortString:String = title + gain.toFullDisplay

  override protected def toFullString:String = toShortString + "\n" + text
}

class LevelUpCard( id:Int,
                   title:String,
                   text:String,
                   cardAction:Option[Action] = Option.empty
                 ) extends TreasureCard( id, title, text, cardAction = cardAction ) {

  override protected def toShortString:String = title

  override protected def toFullString:String = toShortString + "\n" + text
}