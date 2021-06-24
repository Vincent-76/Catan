package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State, StructurePlacement }
import de.htwg.se.catan.model.commands.BuildCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq

import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildState {
  def fromXML( node:Node ):BuildState = BuildState(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get
  )
}

case class BuildState( structure:StructurePlacement ) extends State {

  def toXML:Node = <BuildState structure={ structure.title } />

  override def build( id:Int ):Option[Command] = Some(
    BuildCommand( id, this )
  )

  //override def toString:String = getClass.getSimpleName + ": StructurePlacement[" + structure.title + "]"
}
