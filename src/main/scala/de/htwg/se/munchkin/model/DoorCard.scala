package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.model.PlayerProperties.Properties
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
class DoorCard( id:Int,
                title:String,
                text:String,
                equippable:Boolean = false,
                gain:Int = 0,
                propertyChanges:Properties = Map.empty,
                cardAction:Option[Action] = Option.empty
              ) extends Card( id, title, text, equippable, false, gain, propertyChanges, cardAction ) {

  override def symbol:String = "D"

  override protected def toShortString:String = title + gain.toFullDisplay + propertyChanges.toDisplay

  override protected def toFullString:String = toShortString + "\n" + text

}

class MonsterCard( id:Int,
                   title:String,
                   text:String,
                   val level:Int,
                   val badStuff:String,
                   val treasures:Int,
                   val levelReward:Int = 1,
                   val monsterTypes:Vector[MonsterType] = Vector(),
                   cardAction:Option[Action] = Option.empty,
                   val badStuffAction:BadStuffAction,
                   val fleeAction:Option[FleeAction] = Option.empty
                 ) extends DoorCard( id, title, text, cardAction = cardAction ) {

  override protected def toShortString:String = title + level.toFullDisplay( "Level" )

  override protected def toFullString:String = toShortString +
    monsterTypes.sortBy( _.name ).red( "", ( s:String, mType:MonsterType ) => s + "[" + mType.name + "]" ) + "\n" +
    text + "\n" +
    "Bad Stuff: " + badStuff + "\n" +
    "Reward: " + levelReward.toFullDisplay( "Levels" ) + " " + treasures.toFullDisplay( "Treasures" )
}

sealed abstract class MonsterType( val name:String )

object Undead extends MonsterType( "Undead" )

class CurseCard( id:Int,
                 title:String,
                 text:String,
                 cardAction:Option[Action] = Option.empty
               ) extends DoorCard( id, title, text, cardAction = cardAction ) {

  override protected def toShortString:String = title

  override protected def toFullString:String = toShortString + "\n" + text
}

class CharacterCard( id:Int,
                     title:String,
                     text:String,
                     val skills:Vector[CardSkill],
                     propertyChanges:Properties = Map.empty
                   ) extends DoorCard( id, title, text, true, propertyChanges = propertyChanges ) {

  override protected def toShortString:String = title + propertyChanges.toDisplay

  override protected def toFullString:String = toShortString + "\n" + text +
    skills.red( "", ( s:String, skill:CardSkill ) => s + "\n" + skill.title + ": " + skill.text )
}

class ClassCard( id:Int,
                 title:String,
                 text:String,
                 skills:Vector[CardSkill],
                 propertyChanges:Properties = Map.empty
               ) extends CharacterCard( id, title, text, skills, propertyChanges )

class RaceCard( id:Int,
                title:String,
                text:String,
                skills:Vector[CardSkill],
                propertyChanges:Properties = Map.empty
              ) extends CharacterCard( id, title, text, skills, propertyChanges )

class MonsterEnhancer( id:Int,
                       title:String,
                       text:String,
                       val levelChange:Int,
                       val treasureChange:Int = 0
                     ) extends DoorCard( id, title, text ) {

  override protected def toShortString:String = title + levelChange.toFullDisplay( "Monster" ) + treasureChange.toFullDisplay( "Treasures" )

  override protected def toFullString:String = toShortString + "\n" + text
}

class HelperCard( id:Int,
                  title:String,
                  text:String,
                  gain:Int = 0,
                  propertyChanges:Properties = Map.empty,
                  val sacrificeAction:Action
                ) extends DoorCard( id, title, text, true, gain, propertyChanges ) {

  override protected def toShortString:String = title + gain.toFullDisplay + propertyChanges.toDisplay

  override protected def toFullString:String = toShortString + "\n" + text
}

