package de.htwg.se.catan.util

import de.htwg.se.catan.model.{ Game, Info }

/**
 * @author Vincent76;
 */

trait Observer:
  def onUpdate( game:Game, info:Option[Info] ):Unit

  def onInfo( info:Info ):Unit

  def onError( t:Throwable ):Unit



trait Observable:
  var subscribers:List[Observer] = List.empty

  def add( o:Observer ):Unit = subscribers = subscribers :+ o

  def remove( o:Observer ):Unit = subscribers = subscribers.removed( o ).toList

  def update( game:Game, info:Option[Info] = None ):Unit = subscribers.foreach( _.onUpdate( game, info ) )

  //def info( info:Info ):Unit = subscribers.foreach( _.onInfo( info ) )

  def error( t:Throwable ):Throwable =
    subscribers.foreach( _.onError( t ) )
    t

