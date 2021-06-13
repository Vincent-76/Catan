package de.htwg.se.settlers.model.impl.placement

import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

case object RobberPlacement extends Placement( "Robber" ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] =
    game.gameField.hexList.red( List.empty, ( l:List[Hex], h:Hex ) => {
      if( h != game.gameField.robberHex && h.area.isInstanceOf[LandArea] )
        l :+ h
      else l
    } )
}
