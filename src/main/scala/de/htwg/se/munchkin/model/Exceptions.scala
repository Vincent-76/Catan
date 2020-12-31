package de.htwg.se.munchkin.model

/**
 * @author Vincent76;
 */
class NotFoundException extends Exception

class UnequippableException extends Exception

class PlayerPropertyRequirementException( val playerProperty:PlayerProperty, val required:Int, val free:Int ) extends Exception