package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import collection.mutable

/**
 * @author "Vic Nightfall"
 */
object Grammar {
    
    val factories = mutable.Map[Category, TokenFactory[_]]()
    
    // Predefined Factories
    val nnFactory = new TokenFactory.Word(default = (s, v) => new Entity(s, v))
    val vbFactory = new TokenFactory.Word(default = (s, v) => new Action(s, v))
    val jjFactory = new TokenFactory.Word(default = (s, v) => new Property(s, v))
    
    def init() {
        newFactory(POS.noun, nnFactory)
        newFactory(POS.verb, vbFactory)
        newFactory(POS.adjective, jjFactory)
    }
    
    def factory[T >: Token](ct: Category) : Option[TokenFactory[T]] = factories.get(ct).asInstanceOf[Option[TokenFactory[T]]]
    def newFactory[T >: Token](ct: Category, tf: TokenFactory[T]) = factories += ct -> tf
    
    
}