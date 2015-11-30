package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import collection.mutable
import edu.stanford.nlp.trees.Tree
import moe.nightfall.luithmaenas.NLP
import moe.nightfall.luithmaenas.SentenceMap
import moe.nightfall.luithmaenas.spells.token.TokenFactory
import moe.nightfall.luithmaenas.spells.token.TokenFactory.WordFactory
import moe.nightfall.luithmaenas.spells.token.TokenFactory.SentenceFactory
import moe.nightfall.luithmaenas.spells.token.Token
import moe.nightfall.luithmaenas.spells.token.Object
import moe.nightfall.luithmaenas.spells.token.Property
import moe.nightfall.luithmaenas.spells.token.Action


/**
 * @author "Vic Nightfall"
 */
object Grammar {
    
    val factories = mutable.Map[Category, TokenFactory[_]]()
    
    // Predefined Factories
    val entityFactory   = new WordFactory(() => new Object)
    val actionFactory   = new WordFactory(() => new Action)
    val propertyFactory = new WordFactory(() => new Property)
    
    def init() {
        newFactory(POS.noun, entityFactory)
        newFactory(POS.verb, actionFactory)
        newFactory(POS.adjective, propertyFactory)
        
        newFactory('S, SentenceFactory)
    }
    
    def factory[T <: Token](ct: Category) : Option[TokenFactory[T]] = factories.get(ct).asInstanceOf[Option[TokenFactory[T]]]
    def newFactory[T <: Token](ct: Category, tf: TokenFactory[T]) = factories += ct -> tf
    
    def newEntity(symbol: Symbol, mapper: () => Object) = {
        entityFactory += symbol -> mapper
    }
    
    def newProperty(symbol: Symbol, mapper: () => Property) = {
        propertyFactory += symbol -> mapper
    }
    
    def newAction(symbol: Symbol, mapper: () => Action) = {
        actionFactory += symbol -> mapper
    }
    
    private def parse(s: SentenceMap) : Spell = {
        null
    }
    
    def parse(text: String) : Spell = {
        val doc = NLP.annotate(text)
        var spell: Spell = null
        doc.sentences.foreach { sentence =>
            val s2 = parse(sentence)
            if (spell == null) spell = s2
            else spell += s2
        }
        return spell
    }
}