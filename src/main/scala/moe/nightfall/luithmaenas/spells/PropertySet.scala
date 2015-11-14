package moe.nightfall.luithmaenas.spells

import scala.collection.mutable.TreeSet
import scala.reflect.ClassTag

/**
 * @author "Vic Nightfall"
 */
class PropertySet extends TreeSet[Property]()(ordering = Ordering.by(_.toString)) {
    def find[T <: Property : ClassTag]() : Option[T] = {
        find {case _: T => true}.asInstanceOf[Option[T]]
    }
}