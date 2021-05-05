package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player._
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.aview.gui.guistate._
import de.htwg.se.settlers.util.{ Observer, _ }
import scalafx.application.Platform
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.paint.Color

/**
 * @author Vincent76;
 */
object GUIApp {
  def colorOf( playerColor:PlayerColor ):Color = playerColor match {
    case Green => Color.Green.darker
    case Red => Color.Red.darker
    case Yellow => Color.Yellow.darker
    case Blue => Color.Blue
  }

  def colorOf( f:FieldType ):Color = f match {
    case Water => Color.DodgerBlue
    case Desert => Color.Wheat
    case Wood => Color.Sienna
    case Clay => Color.DarkSalmon
    case Sheep => Color.PaleGreen
    case Wheat => Color.Khaki
    case Ore => Color.DarkGray
    case _ => Color.Black
  }

  def middleOf( p1:(Double, Double), p2:(Double, Double) ):(Double, Double) = {
    val a = (p2._1 - p1._1, p2._2 - p1._2)
    (p1._1 + a._1 / 2, p1._2 + a._2 / 2)
  }

  def middleOf( p1:(Double, Double), p2:(Double, Double), p3:(Double, Double) ):(Double, Double) = {
    val a = (p2._1 - p1._1, p2._2 - p1._2)
    val m = (p1._1 + a._1 / 2, p1._2 + a._2 / 2)
    val r = Math.sqrt( Math.pow( a._1, 2 ) + Math.pow( a._2, 2 ) ) / ( 2 * Math.sqrt( 3 ) )
    val v = (p3._1 - m._1, p3._2 - m._2)
    val vl = Math.sqrt( Math.pow( v._1, 2 ) + Math.pow( v._2, 2 ) )
    (m._1 + r / vl * v._1, m._2 + r / vl * v._2)
  }

  implicit class RichColor( val color:Color ) {
    def toHex:String = "#%02X%02X%02X".format(
      ( color.red * 255 ).toInt,
      ( color.green * 255 ).toInt,
      ( color.blue * 255 ).toInt )
  }

}

class GUIApp( val controller:Controller ) extends Observer {
  val gui:GUI = new GUI( this, controller )
  val thread:Thread = new Thread {
    override def run( ):Unit = {
      gui.main( Array() )
    }
  }
  thread.start()

  controller.add( this )


  def exit( ):Unit = gui.stopApp()


  def getGUIState( state:State ):Option[GUIState] = state match {
    case s:InitState => Some( InitGUIState( controller ) )
    case s:InitPlayerState => Some( InitPlayerGUIState( controller ) )
    case s:InitBeginnerState => Some( InitBeginnerGUIState( s, controller ) )
    case s:BuildInitSettlementState => Some( BuildInitSettlementGUIState( controller ) )
    case s:BuildInitRoadState => Some( BuildInitRoadGUIState( s, controller ) )
    case s:NextPlayerState => Some( NextPlayerGUIState( controller ) )
    case s:DiceState => Some( DiceGUIState( controller ) )
    case s:DropHandCardsState => Some( DropHandCardsGUIState( s, controller ) )
    case s:RobberPlaceState => Some( RobberPlaceGUIState( controller ) )
    case s:RobberStealState => Some( RobberStealGUIState( s, controller ) )
    case s:ActionState => Some( ActionGUIState( controller ) )
    case s:BuildState => Some( BuildGUIState( s, controller ) )
    case s:PlayerTradeState => Some( PlayerTradeGUIState( s, controller ) )
    case s:PlayerTradeEndState => Some( PlayerTradeEndGUIState( s, controller ) )
    case s:YearOfPlentyState => Some( guistate.YearOfPlentyGUIState( controller ) )
    case s:DevRoadBuildingState => Some( DevRoadBuildingGUIState( controller ) )
    case s:MonopolyState => Some( MonopolyGUIState( controller ) )
    case _ => Option.empty
  }


  override def onUpdate( info:Option[Info] ):Unit = {
    gui.update( getGUIState( controller.game.state ) )
    if( info.isDefined )
      onInfo( info.get )
  }

  override def onInfo( info:Info ):Unit = info match {
    case info:DiceInfo =>
      gui.showInfoDialog( info.dices._1 + " + " + info.dices._2 + " = " + ( info.dices._1 + info.dices._2 ) )
    case GatherInfo( dices, playerResources ) =>
      gui.showInfo( playerResources.map( d => controller.player( d._1 ).name.toLength( Game.maxPlayerNameLength ) + "  " + d._2.toString( "+" ) ).mkString( "\n" ) )
      gui.showInfoDialog( dices._1 + " + " + dices._2 + " = " + ( dices._1 + dices._2 ), Some(
        playerResources.toList.sortBy( _._1.id ).map( d => {
          controller.player( d._1 ).name.toLength( Game.maxPlayerNameLength ) + "  " + d._2.toString( "+" )
        } ).mkString( "\n" )
      ) )
    case GotResourcesInfo( pID, cards ) =>
      gui.showInfo( controller.player( pID ).name.toLength( Game.maxPlayerNameLength ) + "  " + cards.toString( "+" ) )
    case LostResourcesInfo( pID, cards ) =>
      gui.showInfo( controller.player( pID ).name.toLength( Game.maxPlayerNameLength ) + "  " + cards.toString( "-" ) )
    case ResourceChangeInfo( playerAdd, playerSub ) =>
      gui.showInfo( ( playerAdd.keys.toSet ++ playerSub.keys.toSet ).toList.sortBy( _.id ).map( pID => {
        val lists = Nil ++
          ( if( playerAdd.contains( pID ) ) List( playerAdd( pID ).toString( "+" ) ) else Nil ) ++
          ( if( playerSub.contains( pID ) ) List( playerSub( pID ).toString( "-" ) ) else Nil )
        controller.player( pID ).name.toLength( Game.maxPlayerNameLength ) + "  " + lists.mkString( ", " )
      } ).mkString( "\n" ) )
    case BankTradedInfo( pID, give, get ) =>
      gui.showInfo( controller.player( pID ).name.toLength( Game.maxPlayerNameLength ) + "  " + give.toString( "" ) + "  <->  " + get.toString( "" ) )
    case DrawnDevCardInfo( _, devCard ) =>
      gui.showInfoDialog( "Drawn: " + devCard.title, Some( devCard.desc ) )
    case InsufficientStructuresInfo( pID, structure ) =>
      gui.showInfo( controller.player( pID ).name + ": Insufficient structures of " + structure.title + " to build more." )
    case NoPlacementPointsInfo( pID, structure ) =>
      gui.showInfo( controller.player( pID ).name +
        ": There aren't any more possible placement points for structure " + structure.title + " to build more." )
    case GameEndInfo( winner ) =>
      val p = controller.player( winner )
      gui.showInfoDialog(
        "Game End",
        Some( p.name + " won with " + controller.game.getPlayerVictoryPoints( p.id ) + " victory points!" ),
        centered = true
      )
    case _ =>
  }


  override def onError( t:Throwable ):Unit = Platform.runLater {
    new Alert( AlertType.Warning ) {
      initOwner( gui.stage )
      headerText = t.getClass.getSimpleName
    }.showAndWait()
  }

}