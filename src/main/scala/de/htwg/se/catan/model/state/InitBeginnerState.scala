package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.{ DiceOutBeginnerCommand, SetBeginnerCommand }

/**
 * @author Vincent76;
 */
case class InitBeginnerState( beginner:Option[PlayerID] = None,
                              diceValues:Map[PlayerID, Int] = Map.empty,
                              counter:Int = 1 ) extends State {

  override def diceOutBeginner():Option[Command] = Some(
    DiceOutBeginnerCommand( this )
  )

  override def setBeginner():Option[Command] = Some(
    SetBeginnerCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": beginner[" + beginner.useOrElse( pID => pID, "-" ) +
    "], DiceValues[" + diceValues.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "], counter[" + counter + "]"*/

}
