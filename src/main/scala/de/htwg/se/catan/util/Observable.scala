package de.htwg.se.catan.util

import de.htwg.se.catan.model.Info

/**
 * @author Vincent76;
 */

trait Observer:
  def onUpdate( info:Option[Info] ):Unit

  def onInfo( info:Info ):Unit

  def onError( t:Throwable ):Unit



trait Observable:
  var subscribers:List[Observer] = List.empty

  def add( o:Observer ):Unit = subscribers = subscribers :+ o

  def remove( o:Observer ):Unit = subscribers = subscribers.removed( o ).toList

  def update( info:Option[Info] = None ):Unit = subscribers.foreach( _.onUpdate( info ) )

  //def info( info:Info ):Unit = subscribers.foreach( _.onInfo( info ) )

  def error( t:Throwable ):Unit = subscribers.foreach( _.onError( t ) )

