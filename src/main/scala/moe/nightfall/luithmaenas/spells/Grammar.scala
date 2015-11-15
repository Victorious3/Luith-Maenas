package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import collection.mutable
import moe.nightfall.luithmaenas.spells.TokenFactory.WordFactory
import moe.nightfall.luithmaenas.spells.TokenFactory.SentenceFactory

/**
 * @author "Vic Nightfall"
 */
object Grammar {
    
    val factories = mutable.Map[Category, TokenFactory[_]]()
    
    // Predefined Factories
    val nnFactory = new WordFactory(default = (s, v) => new Entity(s, v))
    val vbFactory = new WordFactory(default = (s, v) => new Action(s, v))
    val jjFactory = new WordFactory(default = (s, v) => new Property(s, v))
    
    def init() {
        newFactory(POS.noun, nnFactory)
        newFactory(POS.verb, vbFactory)
        newFactory(POS.adjective, jjFactory)
        
        newFactory('S, SentenceFactory)
    }
    
    def factory[T <: Token](ct: Category) : Option[TokenFactory[T]] = factories.get(ct).asInstanceOf[Option[TokenFactory[T]]]
    def newFactory[T <: Token](ct: Category, tf: TokenFactory[T]) = factories += ct -> tf
    
    
}