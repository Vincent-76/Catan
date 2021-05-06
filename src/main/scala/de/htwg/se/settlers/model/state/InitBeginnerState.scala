package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.{Command, State}
import de.htwg.se.settlers.model.commands.{DiceOutBeginnerCommand, SetBeginnerCommand}
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class InitBeginnerState( beginner:Option[PlayerID] = Option.empty,
                              diceValues:Map[PlayerID, Int] = Map.empty,
                              counter:Int = 1 ) extends State {

  override def diceOutBeginner( ):Option[Command] = Some(
    DiceOutBeginnerCommand( this )
  )

  override def setBeginner( ):Option[Command] = Some(
    SetBeginnerCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": beginner[" + beginner.useOrElse( pID => pID, "-" ) +
    "], DiceValues[" + diceValues.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "], counter[" + counter + "]"*/

}
