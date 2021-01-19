package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.YearOfPlentyCommand

/**
 * @author Vincent76;
 */
abstract class YearOfPlentyState( val nextState:State,
                                  controller:Controller ) extends State( controller ) {

  override def yearOfPlentyAction( resources:ResourceCards ):Unit = controller.action(
    YearOfPlentyCommand( resources, this )
  )

}
