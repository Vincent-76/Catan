package de.htwg.se.catan.model.impl.placement

import de.htwg.se.catan.model._
import de.htwg.se.catan.util._

case object RobberPlacement extends Placement( "Robber" ):

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] =
    game.gameField.hexList.red( List.empty, ( l:List[Hex], h:Hex ) => {
      if h != game.gameField.robberHex && h.area.isInstanceOf[LandArea] then
        l :+ h
      else l
    } )