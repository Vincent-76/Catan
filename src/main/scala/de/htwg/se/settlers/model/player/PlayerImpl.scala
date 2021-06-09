package de.htwg.se.settlers.model.player

import de.htwg.se.settlers.model.cards.Cards._
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.cards.{Cards, DevelopmentCard}
import de.htwg.se.settlers.util._

import scala.util.{Failure, Random, Success, Try}

case class PlayerImpl(id: PlayerID,
                      color: PlayerColor,
                      name: String,
                      resources: ResourceCards = Cards.getResourceCards(0),
                      devCards: Vector[DevelopmentCard] = Vector.empty,
                      usedDevCards: Vector[DevelopmentCard] = Vector.empty,
                      victoryPoints: Int = 0,
                      structures: Map[StructurePlacement, Int] = StructurePlacement.get.map(p => (p, p.available)).toMap
                     ) extends Player {

  override def color:PlayerColor = color

  override def idName: String = "<" + id.id + ">" + name

  override def removeResourceCard(resource: Resource, amount: Int = 1): Try[Player] = resources.subtract(resource, amount) match {
    case Success(newResources) => Success(copy(resources = newResources))
    case Failure(e) => Failure(e)
  }

  override def removeResourceCards(cards: ResourceCards): Try[Player] = resources.subtract(cards) match {
    case Success(newResources) => Success(copy(resources = newResources))
    case Failure(e) => Failure(e)
  }

  override def addResourceCard(resource: Resource, amount: Int = 1): Player = copy(resources = resources.add(resource, amount))

  override def addResourceCards(cards: ResourceCards): Player = copy(resources = resources.add(cards))

  override def trade(get: ResourceCards, give: ResourceCards): Try[Player] = addResourceCards(get).removeResourceCards(give)

  override def addDevCard(card: DevelopmentCard): Player = copy(devCards = devCards :+ card)

  override def removeDevCard(): Player = copy(devCards = devCards.init)

  override def addVictoryPoint(): Player = copy(victoryPoints = victoryPoints + 1)

  override def hasStructure(structure: StructurePlacement): Boolean = structures.getOrElse(structure, 0) > 0

  override def getStructure(structure: StructurePlacement): Try[Player] = {
    val available = structures.getOrElse(structure, 0)
    if (available > 0) {
      val newStructures = if (structure.replaces.isDefined)
        structures.updated(structure.replaces.get, structures(structure.replaces.get) + 1)
      else structures
      Success(copy(structures = newStructures.updated(structure, available - 1)))
    } else
      Failure(InsufficientStructures(structure))
  }

  override def addStructure(structure: StructurePlacement): Player = {
    val newStructures = if (structure.replaces.isDefined)
      structures.updated(structure.replaces.get, structures.getOrElse(structure.replaces.get, 0) - 1)
    else structures
    copy(
      structures = newStructures.updated(structure, newStructures.getOrElse(structure, 0) + 1)
    )
  }

  override def randomHandResource(): Option[Resource] = {
    Random.element(resources.red(List.empty, (l: List[Resource], r: Resource, amount: Int) => {
      (0 until amount).red(l, (l: List[Resource], _) => {
        l :+ r
      })
    }))
  }

  override def useDevCard(devCard: DevelopmentCard): Try[Player] = {
    val index = devCards.indexOf(devCard)
    if (index >= 0)
      Success(copy(
        devCards = devCards.removeAt(index),
        usedDevCards = usedDevCards :+ devCard
      ))
    else Failure(InsufficientDevCards(devCard))
  }
}
