package moe.nightfall.luithmaenas.spells

import moe.nightfall.luithmaenas.POS.Category
import moe.nightfall.luithmaenas.POS
import edu.stanford.nlp.trees.Tree
import scala.collection.mutable.ListBuffer
import scala.collection.concurrent.TrieMap

/**
 * @author "Vic Nightfall"
 */
trait Token {
    val properties: PropertySet = new PropertySet
    val children: ListBuffer[Token] = new ListBuffer
    val symbol: Symbol
    
    override def equals(other: Any) = other match {
        case other: Token => other.symbol == this.symbol
        case _ => false
    }
    
    override lazy val hashCode = symbol.hashCode
    
    def isLeaf = children.isEmpty
}

trait TokenFactory[T >: Token] {
    def create(tree: Tree) : T
}

object TokenFactory {
    class Word[T >: Token](val default: (Symbol, Tree) => T) extends TokenFactory[T] {
      val tokens: TrieMap[Symbol, (Symbol, Tree) => T] = new TrieMap
      
      override def create(tree: Tree) : T = {
          val symbol = POS.toSymbol(tree.firstChild.label)
          tokens.get(symbol).getOrElse(default).apply(symbol, tree)
      }
      
      def ++= (factory: TokenFactory.Word[T]) : TokenFactory.Word[T] = {
          tokens ++= factory.tokens
          return this
      }
  
      def += (kv : (Symbol, (Symbol, Tree) => T)) : TokenFactory.Word[T] = {
          tokens += kv
          return this
      }
  }
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