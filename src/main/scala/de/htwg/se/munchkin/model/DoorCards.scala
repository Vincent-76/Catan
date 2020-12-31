package de.htwg.se.munchkin.model

import de.htwg.se.munchkin.Util

import scala.collection.immutable.Queue
import scala.util.Random

/**
 * @author Vincent76;
 */

object DoorCards {

  def random:Queue[DoorCard] = Random.shuffle( cards )

  val cards:Queue[DoorCard] = Queue(
    new MonsterCard(
      1,
      "Maul Rat",
      "A creature from Hell. +3 against Clerics.",
      1,
      "She whacks you. Lose a level.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      1,
      "Lame Goblin",
      "+1 to Run Away",
      1,
      "He whacks you with his crutch. Lose a level.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      2,
      "Pit Bull",
      "If you can't defeat it, you may distract it (automatic escape) by dropping any wand, pole, or staff. (Fetch, Fido!)",
      2,
      "Fang marks in your butt. Lose 2 levels",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      3,
      "Flying Frogs",
      "-1 to Run Away.",
      2,
      "They bite! Lose 2 levels.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      4,
      "Mr. Bones",
      "If you must flee, you lose a level even if you escape.",
      2,
      "His bony touch costs you 2 levels.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      5,
      "Large Angry Chicken",
      "Fried chicken is delicious. Gain an extra level if you defeat it with fire or flame.",
      2,
      "Very painful pecking. Lose a level.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      6,
      "Gelatinous Octahedron",
      "+1 to Run Away.",
      2,
      "Drop all your Big items.",
      1,
      badStuffAction = null
    ),
    new MonsterCard(
      7,
      "Harpies",
      "They resist magic. +5 against Wizards",
      4,
      "Their music is really, really bad. Lose 2 levels.",
      2,
      badStuffAction = null
    ),
    new MonsterCard(
      8,
      "Undead Horse",
      "+5 against Dwarves.",
      4,
      "Kicks, bites, and smells awful. Lose 2 levels.",
      2,
      monsterTypes = Vector( Undead ),
      badStuffAction = null
    ),
    new MonsterCard(
      9,
      "Snails on Speed",
      "-2 to Run Away",
      4,
      "They steal your treasure. Roll a die and lose that many items or cards in your hand - your choice.",
      2,
      badStuffAction = null
    ),
    new MonsterCard(
      10,
      "Leperchaun",
      "He's gross! +5 against Elves.",
      4,
      "He takes two items from you - one chosen by the player on either side of you.",
      2,
      badStuffAction = null
    ),
    new MonsterCard(
      11,
      "Lawyers",
      "Will not attack a Thief (professional courtesy). A Thief encountering a lawyer my instead discard two Treasures and draw two new ones face down.",
      6,
      "He hits you with an injunction. Let each other player draw one card from your hand, starting with the player to your left. Discard any remainder.",
      2,
      badStuffAction = null
    ),
    new MonsterCard(
      12,
      "Pukachu",
      "Gain an extra level if you defeat it without using help or bonuses.",
      6,
      "Projectile vomiting attack! Discard your whole hand.",
      2,
      badStuffAction = null
    ),
    new MonsterCard(
      13,
    )
  )
}