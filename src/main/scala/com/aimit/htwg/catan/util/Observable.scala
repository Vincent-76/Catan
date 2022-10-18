package com.aimit.htwg.catan.util

import com.aimit.htwg.catan.model.Info

/**
 * @author Vincent76;
 */

trait Observer {
  def onUpdate( info:Option[Info] ):Unit

  def onInfo( info:Info ):Unit

  def onError( t:Throwable ):Unit
}


trait Observable {
  var subscribers:List[Observer] = List.empty

  def add( o:Observer ):Unit = subscribers = subscribers :+ o

  def remove( o:Observer ):Unit = subscribers = subscribers.removed( o ).toList

  def update( info:Option[Info] = None ):Option[Info] = {
    subscribers.foreach( _.onUpdate( info ) )
    info
  }

  //def info( info:Info ):Unit = subscribers.foreach( _.onInfo( info ) )

  def error( t:Throwable ):Throwable = {
    subscribers.foreach( _.onError( t ) )
    t
  }
}
