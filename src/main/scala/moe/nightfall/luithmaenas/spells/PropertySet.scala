package moe.nightfall.luithmaenas.spells

import scala.collection.mutable.TreeSet
import scala.reflect.ClassTag
import moe.nightfall.luithmaenas.spells.token.Token

/**
 * @author "Vic Nightfall"
 */
class PropertySet extends TreeSet[Token]()(ordering = Ordering.by(_.toString)) {
    def find[T <: Token : ClassTag]() : Option[T] = {
        find {case _: T => true}.asInstanceOf[Option[T]]
    }
}