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
    
    // Private variables
    private var _symbol: Symbol = _ 
    private var _factory: TokenFactory[_] = _
    private var _parent: Token = _
    private var _tree: Tree = _
    
    /** Contains the symbol that was used to construct this tokem */
    def symbol = _symbol
    /** Contains the factory that was used to construct this token */
    def factory = _factory
    /** Contains the parent token */
    def parent = _parent
    
    def before() : this.type = this
    def after() : this.type = this
    
    final override def equals(other: Any) = other match {
        case other: this.type => other.symbol == this.symbol
        case _ => false
    }
    
    final override def hashCode = symbol.hashCode
    
    final def isLeaf = children.isEmpty
    final def isRoot = parent != null
}

object Token {
    /** Used to create a new token instance **/
    def apply[T <: Token](constructor: () => T, factory: TokenFactory[_], parent: Token, tree: Tree, symbol: Symbol = Symbol("")): T = {
        val token = constructor()
        token._symbol = symbol
        token._factory = factory
        token._parent = parent
        token._tree = tree
        return token
    }
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
    final def create(tree: Tree, parent: Token) : T = {
        var token = createImpl(tree, parent).before()
        before foreach {converter =>
            token = converter(token)
        }
        return token
    }
    
    def createImpl(tree: Tree, parent: Token) : T
    
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
        
        override def createImpl(tree: Tree, parent: Token) : T = {
            val symbol = POS.toSymbol(tree.firstChild.label)
            return Token(
               constructor = tokens.get(symbol).getOrElse(default), 
               symbol      = symbol, 
               factory     = this, 
               parent      = parent, 
               tree        = tree
            )
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
        override def createImpl(tree: Tree, parent: Token) : Sentence = {
            Token(() => new Sentence(), this, parent, tree)
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