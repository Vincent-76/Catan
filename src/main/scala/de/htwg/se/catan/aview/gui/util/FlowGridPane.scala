package de.htwg.se.catan.aview.gui.util

import scalafx.scene.Node
import scalafx.scene.layout.{ ColumnConstraints, GridPane }

/**
 * @author Vincent76;
 */
class FlowGridPane( columns:Int, spanLast:Boolean = false ) extends GridPane {
  columnConstraints = (1 to columns).map( _ => new ColumnConstraints {
    percentWidth = 100 / columns
  } )

  def addAll( c:Seq[Node] ):Unit = addIn( c.iterator, 0, 0 )

  private def addIn( i:Iterator[Node], c:Int, r:Int ):Unit = {
    if( i.hasNext ) {
      val n = i.next()
      if( spanLast && !i.hasNext )
        add( n, c, r, columns - c, 1 )
      else
        add( n, c, r )
      if( (c + 1) < columns )
        addIn( i, c + 1, r )
      else
        addIn( i, 0, r + 1 )
    }
  }
}
