package moe.nightfall.luithmaenas.spells

import scala.collection.mutable
import edu.stanford.nlp.trees.Tree
import moe.nightfall.luithmaenas.SentenceMap
import edu.stanford.nlp.ling.Label
import edu.stanford.nlp.ling.IndexedWord
import moe.nightfall.luithmaenas.spells.token.Token
import moe.nightfall.luithmaenas.spells.token.Action

/**
 * @author "Vic Nightfall"
 */
class Spell extends Action {
    /** Map of all processed tokens of this spell */
    val tokenMap = mutable.Map[IndexedWord, Token]()
    
    def token(word: IndexedWord) = tokenMap.get(word)
}