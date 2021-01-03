package de.htwg.se.munchkin.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

/**
 * @author Vincent76;
 */
class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" when {
    val equippedCard = new ClassCard( 1, "Title", "Text", Vector(), Map( RunAway -> 2, Hand -> 1 ) )
    val equippedCard2 = new TreasureCard( 2, "Title2", "Text2", true, false, 4, Map( Head -> -1 ) )
    val equippedCard3 = new TreasureCard( 3, "EquipCard", "Text", true, false, 3, Map( Hand -> -3 ) )
    val handCard = new TreasureCard( 4, "Title", "Text", true, false, 3 )
    val player = Player( 5, "Bob", Male, Vector( handCard, equippedCard3 ), Vector( equippedCard, equippedCard2 ) )
    "new" should {
      "have a name" in {
        player.name should be( "Bob" )
      }
      "have level" in {
        player.level should be( 1 )
      }
      "have equipped" in {
        player.equipped should equal( Vector( equippedCard, equippedCard2 ) )
      }
      "have on hand" in {
        player.hand( 0 ) should be( handCard )
      }
      "have totalGain" in {
        player.totalGain should be( 4 )
      }
      "have combatLevel" in {
        player.combatLevel should be( 5 )
      }
      "have new level" in {
        player.changeLevel( 2 ).level should be( 3 )
      }
      "have properties" in {
        player.properties( Head ) should be( Head.defaultValue - 1 )
        player.properties( Armor ) should be( Armor.defaultValue )
        player.properties( Hand ) should be( Hand.defaultValue + 1 )
        player.properties( Boots ) should be( Boots.defaultValue )
        player.properties( Big ) should be( Big.defaultValue )
        player.properties( RunAway ) should be( RunAway.defaultValue + 2 )
      }
    }
    val player2:Try[Player] = player.equipCard( equippedCard3 )
    "reequipped" should {
      "be successful" in {
        player2.isSuccess should be( true )
      }
      "have properties" in {
        player2.get.properties( Hand ) should be( player.properties( Hand ) - 3 )
      }
    }
  }
}
