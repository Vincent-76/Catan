package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
sealed abstract class PlayerProperty( val name:String, val defaultValue:Int, val min:Option[Int] )

case object Class extends PlayerProperty( "Class", 1, Some( 0 ) )

case object Race extends PlayerProperty( "Race", 1, Some( 0 ) )

case object Head extends PlayerProperty( "Head", 1, Some( 0 ) )

case object Armor extends PlayerProperty( "Armor", 1, Some( 0 ) )

case object Hand extends PlayerProperty( "Hand", 2, Some( 0 ) )

case object Boots extends PlayerProperty( "Boots", 1, Some( 0 ) )

case object Big extends PlayerProperty( "Big", 1, Some( 0 ) )

case object RunAway extends PlayerProperty( "RunAway", 2, Option.empty )

object PlayerProperties {
  type Properties = Map[PlayerProperty, Int]

  private def getDefault:Properties = Vector( Head, Armor, Hand, Boots, Big, RunAway ).map( e => e -> e.defaultValue ).toMap

  def checkCards( cards:Seq[Card], properties:Properties = getDefault ):Properties = {
    cards.red( properties, ( e:Properties, a:Card ) => a.getPropertyChanges.red( e, ( e:Properties, property:PlayerProperty, change:Int ) => {
        val value = if ( !e.contains( property ) )
          property.defaultValue
        else
          e( property )
        e + ( property -> ( value + change ) )
      } ) )
  }
}
