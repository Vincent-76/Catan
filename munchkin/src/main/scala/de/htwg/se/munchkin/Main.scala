package de.htwg.se.munchkin

import de.htwg.se.munchkin.controller.Controller

/**
 * @author Vincent76;
 */
object Main {
  def main( args:Array[String] ):Unit = {
    new Controller()
  }

  /*def main( args:Array[String] ): Unit = {
    val bob = new Player( "Bob",
      Vector( new DoorCard( "Zauberer", "Eine Zauberer Klasse" ),
        new TreasureCard( "Axt", "Eine Axt" ),
        new TreasureCard( "Helm", "Ein Helm" ) ),
      Vector( new DoorCard( "Test", "test" ),
        new DoorCard( "Test2", "test2" ),
        new TreasureCard( "Test3", "test3" ),
        new DoorCard( "Test4", "test4" ) )
    )

    val tom = new Player( "Tom",
      Vector( new DoorCard( "Dieb", "Eine Dieb Klasse" ),
        new DoorCard( "Elf", "Eine Elf Rasse" ),
        new TreasureCard( "Bogen", "Ein Bogen" ) ),
      Vector( new TreasureCard( "Test5", "test5" ),
        new DoorCard( "Test6", "test6" ),
        new TreasureCard( "Test7", "test7" ) )
    )

    val players = Vector( bob, tom )

    val lastDoorCard = new MonsterCard( "Psycho-Squirrel",
      "Doesn't attack female players or male players wearing spiky genital protectors.",
      3,
      0,
      "Lose 1 level, speak in a high squeaky voice until your next move.",
      1
    )
    val lastTreasureCard = new TreasureCard( "Schweizer Armee-Hellebarde", "Nur von Menschen nutzbar" )

    construct( 0, players, lastDoorCard, lastTreasureCard )
  }


  def construct( active:Int = 0, players:Vector[Player], lastDoorCard:DoorCard, lastTreasureCard:TreasureCard ):Unit = {
    println( "TreasureCard:" )
    lastTreasureCard.display
    println()
    println( "DoorCard:" )
    lastDoorCard.display
    println()
    println()
    if( players.nonEmpty ) {
      for ( i <- players.indices ) {
        players( i ).display( i == 0 )
        println()
      }
    }
  }*/
}
