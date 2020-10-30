class Card( title:String, text:String ) {
  def display:Unit = {
    printf( "%n %t %s", title )
    printf( "%n %t %s", text )
  }

  def displayShort:String = title

  def cardType:String = "?"
}

class DoorCard( title:String, text:String ) extends Card( title, text ) {
  override def cardType:String = "D"
}

class MonsterCard( title:String,
                   text:String,
                   level:Int,
                   mType:Int,
                   badThings:String,
                   treasures:Int,
                   levelReward:Int = 1 )
  extends DoorCard( title, text ) {

  override def display:Unit = {
    println( "Level " + level )
    if( mType > 0 ) println( "Type" + mType )
    super.display
    println( "Bad Things: " + badThings )
    println( "Levels: " + levelReward )
    println( "Treasures: " + treasures )
  }
}

class ClassSkill( val title:String, val text:String, val action: () )

class ClassCard( title:String,
                 text:String,
                 skills:Vector[ClassSkill] )
  extends DoorCard( title, text ) {

  override def display:Unit = {
    super.display
    for( skill <- skills ) {
      println( skill.title + ": " + skill.text )
    }
  }
}


class TreasureCard( title:String, text:String ) extends Card( title, text ) {
  override def cardType:String = "T"
}



class Player( val name:String, val equipped: Vector[Card], val hand: Vector[Card] ) {
  def display( active:Boolean ):Unit = {
    println( ( if( active ) "-> " else "" ) + name )
    printf( "Equipped:" )
    for( card <- equipped ) {
      print( " " + card.displayShort )
    }
    printf( "%nHand:" )
    for( card <- hand ) {
      print( " " + card.cardType )
    }
  }
}




def construct( active:Int = 0, players:Vector[Player], lastDoorCard:DoorCard, lastTreasureCard:TreasureCard ):String = {
  var result = "TreasureCard: " + lastTreasureCard.display
  result = result + "\n\n" + "DoorCard: " + lastDoorCard.display
  if( players.nonEmpty ) {
    for ( i <- 0 to players.length ) {
      result = result + "\n\n" + players( i ).display( active == i )
    }
  }
  result
}

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
  "Doesn't attack female players or male players wearing spiky genital protectors",
  3,
  0,
  "Lose 1 level, speak in a high squeaky voice until your next move.",
  1
)
val lastTreasureCard = new TreasureCard( "Schweizer Armee-Hellebarde", "Nur von Menschen nutzbar" )

//println( construct( 0, players, lastDoorCard, lastTreasureCard ) )

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