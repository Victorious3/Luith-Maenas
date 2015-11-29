package moe.nightfall.luithmaenas.spells.token

import moe.nightfall.luithmaenas.POS
import scala.collection.JavaConversions
import scala.collection.concurrent.TrieMap
import edu.stanford.nlp.ling.CoreLabel
import moe.nightfall.luithmaenas.SentenceMap
import edu.stanford.nlp.ling.IndexedWord
import scala.collection.mutable.ListBuffer
import moe.nightfall.luithmaenas.Actor
import moe.nightfall.luithmaenas.spells.PropertySet
import moe.nightfall.luithmaenas.spells.Spell

/**
 * @author "Vic Nightfall"
 */
trait Token {
    val properties: PropertySet = new PropertySet
    
    private var _context: Context = _
    private var _symbol: Symbol = _
    private var _name: String = _
    
    /** Context of this token, only available while parsing, returns null on the server side */
    def context = _context 
    /** Contains the symbol that was used to construct this token */
    def symbol = _symbol
    /** Name of this token */
    def name = _name

    def context(context: Context) : this.type = {
        this._context = context
        this._symbol = context.symbol
        this._name = context.name
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
    final def isRoot = context.parent != null
}

/** The `Context` of a token contains all relevant information to parse a token. */
case class Context (
    /** The factory that was used to construct this token */
    factory: TokenFactory[Token],
    /** The spell that constructed this token */
    root: Spell,
    /** The parent of this token, or null if this is the root node */
    parent: Token,
    /** The token associated to this context */
    self: Token,
    /** The label, contains grammatical information */
    label: CoreLabel,
    /** The sentence, contains the dependency tree and the relations */
    sentence: SentenceMap
) {
    val symbol = POS.toSymbol(label)
    val name = label.value
    val word = new IndexedWord(label)
    
    val incoming = JavaConversions.asScalaBuffer(dependencies.getIncomingEdgesSorted(word))
    val outgoing = JavaConversions.asScalaBuffer(dependencies.getOutEdgesSorted(word))
    
    def dependencies = sentence.dependencies
    
    def incoming(relation: String) : Option[Token] = {
        incoming.find(_.getRelation.getShortName == relation).flatMap(edge => token(edge.getGovernor))
    }
    
    def outgoing(relation: String) : Option[Token] = {
        outgoing.find(_.getRelation.getShortName == relation).flatMap(edge => token(edge.getDependent))
    }
    
    def token(word: IndexedWord) = root.token(word)
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
            return tokens.get(context.symbol).getOrElse(default).apply()
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
            new Sentence()
        }
    }
}

class Sentence extends Token

class Entity extends Token

class Property extends Token

trait Target extends Property {
    
}

trait Condition extends Property {
    def apply(actor: Actor) : Boolean
}

class Action extends Property {
    
    val subActions: ListBuffer[Action] = ListBuffer()
    
    final def += (action: Action): Action = {
        subActions += action
        return this
    }
    
    def act(actor: Actor) = {}
    
    lazy val condition: Option[Condition] = properties.find[Condition]
}
