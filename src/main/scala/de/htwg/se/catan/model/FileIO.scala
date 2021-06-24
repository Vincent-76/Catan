package de.htwg.se.catan.model

trait FileIO {

  def load( path:String ):Game

  def save( game:Game ):String
}
