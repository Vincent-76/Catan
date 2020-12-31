package de.htwg.se.munchkin.model

import scala.collection.immutable.Queue
import scala.util.Random
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
case class Game( doorQueue:Queue[DoorCard],
                 treasureQueue:Queue[TreasureCard],
                 phase:Phase = InitPhase,
                 players:Vector[Player] = Vector(),
                 turn:Int = 0,
                 doorStack:List[DoorCard] = List(),
                 treasureStack:List[TreasureCard] = List() ) {

  def turnPlayer:Player = players( turn )

  def setPhase( phase:Phase ):Game = copy( phase = phase )

  def updatePlayer( id:Int, nPlayer:Player ):Game = copy( players = players.updated( id, nPlayer ) )

  def drawDoor:(Option[DoorCard], Game) = {
    val (cards, game) = drawDoor()
    (if( cards .nonEmpty ) Some( cards.head ) else Option.empty, game)
  }

  def drawDoor( amount:Int = 1 ):(Iterable[DoorCard], Game) = {
    if ( amount >= doorQueue.size ) {
      val (queue, stack) = shuffle( doorQueue, doorStack )
      if ( queue.size < amount )
        return (queue, copy( doorQueue = Queue(), doorStack = stack ))
      val (drawn, rest) = queue.splitAt( amount )
      return (drawn, copy( doorQueue = rest, doorStack = stack ))
    }
    val (drawn, rest) = doorQueue.splitAt( amount )
    (drawn, copy( doorQueue = rest ))
  }

  def drawTreasure:(Option[TreasureCard], Game) = {
    val (cards, game) = drawTreasure()
    (if( cards .nonEmpty ) Some( cards.head ) else Option.empty, game)
  }
  def drawTreasure( amount:Int = 1 ):(Iterable[TreasureCard], Game) = {
    if ( amount >= treasureQueue.size ) {
      val (queue, stack) = shuffle( treasureQueue, treasureStack )
      if ( queue.size < amount )
        return (queue, copy( treasureQueue = Queue(), treasureStack = stack ))
      val (drawn, rest) = queue.splitAt( amount )
      return (drawn, copy( treasureQueue = rest, treasureStack = stack ))
    }
    val (drawn, rest) = treasureQueue.splitAt( amount )
    (drawn, copy( treasureQueue = rest ))
  }

  private def shuffle[A <: Card]( queue:Queue[A], stack:List[A] ):(Queue[A], List[A]) = {
    if ( stack.nonEmpty )
      (queue ++ Random.shuffle( stack.tail ), stack.head :: Nil)
    (queue, stack)
  }

  def dropCard( card:Card ):Game = dropCards( card :: Nil )
  def dropCards( cards:Iterable[Card] ):Game = copy( doorStack = cards.withType[DoorCard] ++: doorStack, treasureStack = cards.withType[TreasureCard] ++: treasureStack )
}
