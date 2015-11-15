package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import collection.mutable
import moe.nightfall.luithmaenas.spells.TokenFactory.WordFactory
import moe.nightfall.luithmaenas.spells.TokenFactory.SentenceFactory
import edu.stanford.nlp.trees.Tree

/**
 * @author "Vic Nightfall"
 */
object Grammar {
    
    val factories = mutable.Map[Category, TokenFactory[_]]()
    
    // Predefined Factories
    val entityFactory   = new WordFactory(() => new Entity)
    val actionFactory   = new WordFactory(() => new Action)
    val propertyFactory = new WordFactory(() => new Property)
    
    def init() {
        newFactory(POS.noun, entityFactory)
        newFactory(POS.verb, actionFactory)
        newFactory(POS.adjective, propertyFactory)
        
        newFactory('ROOT, SpellFactory)
        newFactory('S, SentenceFactory)
    }
    
    def factory[T <: Token](ct: Category) : Option[TokenFactory[T]] = factories.get(ct).asInstanceOf[Option[TokenFactory[T]]]
    def newFactory[T <: Token](ct: Category, tf: TokenFactory[T]) = factories += ct -> tf
    
    def newEntity(symbol: Symbol, mapper: () => Entity) = {
        entityFactory += symbol -> mapper
    }
    
    def newProperty(symbol: Symbol, mapper: () => Property) = {
        propertyFactory += symbol -> mapper
    }
    
    def newAction(symbol: Symbol, mapper: () => Action) = {
        actionFactory += symbol -> mapper
    }
}