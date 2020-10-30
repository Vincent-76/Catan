package model

/**
 * @author Vincent76;
 */
/*def wrapText( text:String ):Unit = {
  var result = ""
  for ( i <- 0 to text.length ) {
    if ( i != 0 ) {
      if( text.charAt( i ) == ' ' ) {
        val pos = i % 20
        for( j <- ( i + 1 ).until( i + 20 - pos ) ) {
          if( text.charAt( j ) == ' ' ) {

          }
        }
      }
    }
    result
  }
}*/

class Card( title:String, text:String ) {
  def display:Unit = {
    println( "\t" + title )
    println( "\t" + text )
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
    println( "\tLevel " + level )
    if( mType > 0 ) println( "\tType" + mType )
    super.display
    println( "\tBad Things: " + badThings )
    println( "\tLevels: " + levelReward )
    println( "\tTreasures: " + treasures )
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
      println( "\t" + skill.title + ": " + skill.text )
    }
  }
}


class TreasureCard( title:String, text:String ) extends Card( title, text ) {
  override def cardType:String = "T"
}
