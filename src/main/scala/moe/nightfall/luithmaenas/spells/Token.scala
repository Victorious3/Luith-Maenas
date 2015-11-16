package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import edu.stanford.nlp.trees.Tree
import scala.collection.mutable.ListBuffer
import scala.collection.concurrent.TrieMap
import scala.reflect.internal.Symbols
import moe.nightfall.luithmaenas.SentenceMap
import moe.nightfall.luithmaenas.SentenceMap
import moe.nightfall.luithmaenas.SentenceMap
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.ling.Label

/**
 * @author "Vic Nightfall"
 */
trait Token {
    val properties: PropertySet = new PropertySet
    
    private var _context: Context = _
    
    def context = _context
    /** Contains the symbol that was used to construct this token */
    def symbol = context.symbol
    /** Contains the factory that was used to construct this token */
    def factory = context.factory
    /** Contains the parent token */
    def parent = context.parent
    
    def apply(context: Context) : this.type = {
        this._context = context
        return this
    }
    
    def before() : this.type = this
    def after() : this.type = this
    
    final override def equals(other: Any) = other match {
        case other: this.type => other.symbol == this.symbol
        case _ => false
    }
    
    final override def hashCode = symbol.hashCode
    
    final def isLeaf = properties.isEmpty
    final def isRoot = parent != null
}

case class Context (
    val tree: Tree,
    val factory: TokenFactory[Token],
    val parent: Token,
    val sentence: SentenceMap
) {
    val label = new CoreLabel(tree.label())
    val symbol = POS.toSymbol(label)
}


/**
 * `TokenFactory` is used to create `Token`. 
 * You can add listeners that allow you to modify existing tokens easily.
 */
trait TokenFactory[T <: Token] {
    
    /** List of pre-converters, use this if you need more control over the order */
    val before = ListBuffer[T => T]()
    /** List of post-converters, use this if you need more control over the order */
    val after = ListBuffer[T => T]()
    
    /** Creates a new token **/
    final def create(context: Context) : T = {
        var token = createImpl(context).before()
        before foreach {converter =>
            token = converter(token)
        }
        return token
    }
    
    def createImpl(context: Context) : T
    
    /** Adds a new listener to the token factory that gets invoked upon walking the
     *  tree inwards, e.g before constructing the children
     */
    def before(converter: T => T) {
        before += converter
    }
    
    /** adds a new listener to the token factory that gets invoked upon walking the
     *  tree outwards, e.g after constructing the children
     */
    def after(converter: T => T) {
        after += converter
    }
}

object TokenFactory {
    class WordFactory[T <: Token](val default: () => T) extends TokenFactory[T] {
        val tokens: TrieMap[Symbol, () => T] = new TrieMap
        
        override def createImpl(context: Context) : T = {
            val token = tokens.get(context.symbol).getOrElse(default).apply()
            return token(context)
        }
        
        def ++= (factory: WordFactory[T]) : WordFactory[T] = {
            tokens ++= factory.tokens
            return this
        }
    
        def += (kv : (Symbol, () => T)) : WordFactory[T] = {
            tokens += kv
            return this
        }
    }
    
    object SentenceFactory extends TokenFactory[Sentence] {
        override def createImpl(context: Context) : Sentence = {
            new Sentence().apply(context)
        }
    }
}

class Sentence extends Token

class Entity extends Token

class Property extends Token


trait Combineable extends Property {
    def += (property: this.type): this.type = this
}

trait Target extends Property {
    
}

class Action extends Property with Combineable {
    
    val subActions: ListBuffer[Action] = ListBuffer()
    
    final override def += (property: this.type): this.type = {
        subActions += property
        return this
    }
    
    lazy val condition: Option[Condition] = properties.find[Condition]
}