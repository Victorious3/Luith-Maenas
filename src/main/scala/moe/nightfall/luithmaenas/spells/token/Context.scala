package moe.nightfall.luithmaenas.spells.token

import moe.nightfall.luithmaenas.spells.Spell
import edu.stanford.nlp.ling.CoreLabel
import moe.nightfall.luithmaenas.SentenceMap
import moe.nightfall.luithmaenas.POS
import scala.collection.JavaConversions
import edu.stanford.nlp.ling.IndexedWord

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