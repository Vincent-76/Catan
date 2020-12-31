package de.htwg.se.munchkin.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author Vincent76;
 */
class CardSpec extends AnyWordSpec with Matchers {
  "A DoorCard" when {
    "new" should {
      val card = new DoorCard( 1, "Title", "Text", false )
      "have a title" in {
        card.title should be( "Title" )
      }
      "have a text" in {
        card.text should be( "Text" )
      }
      "not be equippable" in {
        card.equippable should be( false )
      }
    }
  }
  "A MonsterCard" when {
    "new" should {
      val card = new MonsterCard( 1, "Title", "Text", 5, "BadStuff", 2, badStuffAction = ??? )
      "have a title" in {
        card.title should be( "Title" )
      }
      "have a text" in {
        card.text should be( "Text" )
      }
      "have a level" in {
        card.level should be( 5 )
      }
      "have monster types" in {
        card.monsterTypes should be( Vector() )
      }
      "have bad stuff" in {
        card.badStuff should be( "BadStuff" )
      }
      "have treasures" in {
        card.treasures should be( 2 )
      }
      "should not be equippable" in {
        card.equippable should be( false )
      }
    }
  }
  "A CardSkill" when {
    "new" should {
      val classSkill = new CardSkill( "Title", "Text", ??? )
      "have a title" in {
        classSkill.title should be( "Title" )
      }
      "have a text" in {
        classSkill.text should be( "Text" )
      }
    }
  }
  "A ClassCard" when {
    "new" should {
      val card = new ClassCard( 1, "Title", "Text", Vector() )
      "have a title" in {
        card.title should be( "Title" )
      }
      "have a text" in {
        card.text should be( "Text" )
      }
      "should be equippable" in {
        card.equippable should be( true )
      }
    }
  }
  "A TreasureCard" when {
    "new" should {
      val card = new TreasureCard( 1, "Title", "Text", true )
      "have a title" in {
        card.title should be( "Title" )
      }
      "have a text" in {
        card.text should be( "Text" )
      }
      "should be equippable" in {
        card.equippable should be( true )
      }
    }
  }
}
