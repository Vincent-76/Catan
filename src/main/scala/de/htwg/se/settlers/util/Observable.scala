package de.htwg.se.settlers.util

import de.htwg.se.settlers.model.Info

/**
 * @author Vincent76;
 */

trait Observer {
  def onUpdate():Unit

  def onInfo( info:Info ):Unit

  def onError( t:Throwable )
}


class Observable {
  var subscribers:List[Observer] = List.empty

  def add( o:Observer ):Unit = subscribers = subscribers :+ o

  def remove( o:Observer ):Unit = subscribers = subscribers.removed( o ).toList

  def update():Unit = subscribers.foreach( _.onUpdate() )

  def info( info:Info ):Unit = subscribers.foreach( _.onInfo( info ) )

  def error( t:Throwable ):Unit = subscribers.foreach( _.onError( t ) )
}
