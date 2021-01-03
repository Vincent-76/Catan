package de.htwg.se.munchkin.model

import scala.collection.immutable.Queue
import scala.util.Random

/**
 * @author Vincent76;
 */
object TreasureCards {
  val cards:Queue[TreasureCard] = Queue(

  )

  def random:Queue[TreasureCard] = Random.shuffle( cards )
}

