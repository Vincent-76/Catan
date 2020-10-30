package model

/**
 * @author Vincent76;
 */
class Player( val name:String, val equipped: Vector[Card], val hand: Vector[Card] ) {
  def display( active:Boolean ):Unit = {
    println( ( if( active ) "-> " else "" ) + name )
    printf( "\tEquipped:" )
    for( card <- equipped ) {
      print( " " + card.displayShort )
    }
    print( "\n\tHand:" )
    for( card <- hand ) {
      print( " " + card.cardType )
    }
    println()
  }
}
