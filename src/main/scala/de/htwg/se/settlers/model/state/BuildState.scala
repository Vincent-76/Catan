package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{ Command, State, StructurePlacement }
import de.htwg.se.settlers.model.commands.BuildCommand

/**
 * @author Vincent76;
 */
case class BuildState( structure:StructurePlacement ) extends State {

  override def build( id:Int ):Option[Command] = Some(
    BuildCommand( id, this )
  )

  //override def toString:String = getClass.getSimpleName + ": StructurePlacement[" + structure.title + "]"
}
