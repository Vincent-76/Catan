package de.htwg.se.munchkin.model

/**
 * @author Vincent76;
 */
sealed abstract class Phase( val name:String )

case object InitPhase extends Phase( "Init Phase" )

case object PlayerPhase extends Phase( "Player Phase" )

case object TurnPhase extends Phase( "TurnPhase" )

case object BeginPhase extends Phase( "Begin Phase" )

case object DoorPhase extends Phase( "Door Phase" )

case object FightPhase extends Phase( "Fight Phase" )

case object CursePhase extends Phase( "Curse Phase" )

case object SecondDoorPhase extends Phase( "Second Door Phase" )

case object LootPhase extends Phase( "Loot Phase" )

case object ChallengePhase extends Phase( "Challenge Phase" )

case object EndPhase extends Phase( "End Phase" )

case object GameEndPhase extends Phase( "Game End Phase" )