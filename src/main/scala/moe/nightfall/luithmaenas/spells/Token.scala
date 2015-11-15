package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import edu.stanford.nlp.trees.Tree
import scala.collection.mutable.ListBuffer
import scala.collection.concurrent.TrieMap
import scala.reflect.internal.Symbols

/**
 * @author "Vic Nightfall"
 */
trait Token {
    val properties: PropertySet = new PropertySet
    val children: ListBuffer[Token] = new ListBuffer
    val symbol: Symbol
    
    /** Contains the factory that was used to construct this token */
    var factory: TokenFactory[this.type] = _
    /** Contains the parent token */
    var parent: Token = _
    
    final override def equals(other: Any) = other match {
        case other: this.type => other.symbol == this.symbol
        case _ => false
    }
    
    final override lazy val hashCode = symbol.hashCode
    
    final def isLeaf = children.isEmpty
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
    
    def create(tree: Tree) : T
    
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
    class WordFactory[T <: Token](val default: (Symbol, Tree) => T) extends TokenFactory[T] {
        val tokens: TrieMap[Symbol, (Symbol, Tree) => T] = new TrieMap
        
        override def create(tree: Tree) : T = {
            val symbol = POS.toSymbol(tree.firstChild.label)
            tokens.get(symbol).getOrElse(default).apply(symbol, tree)
        }
        
        def ++= (factory: WordFactory[T]) : WordFactory[T] = {
            tokens ++= factory.tokens
            return this
        }
    
        def += (kv : (Symbol, (Symbol, Tree) => T)) : WordFactory[T] = {
            tokens += kv
            return this
        }
    }
    
    object SentenceFactory extends TokenFactory[Sentence] {
        override def create(tree: Tree) : Sentence = new Sentence(tree)
    }
}

class Sentence(val tree: Tree) extends Token {
    override val symbol = Symbol("")
}

class Entity(val symbol: Symbol, tree: Tree) extends Token

class Property(val symbol: Symbol, tree: Tree) extends Token

trait Combineable extends Property {
    def += (property: this.type): this.type = this
}

trait Target extends Property {
    
}

class Action(symbol: Symbol, tree: Tree) extends Property(symbol, tree) with Combineable {
    
    val subActions: ListBuffer[Action] = ListBuffer()
    
    final override def += (property: this.type): this.type = {
        subActions += property
        return this
    }
    
    lazy val condition: Option[Condition] = properties.find[Condition]
}