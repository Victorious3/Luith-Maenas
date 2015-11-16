package moe.nightfall.luithmaenas.spells

import edu.stanford.nlp.trees.Tree
import moe.nightfall.luithmaenas.SentenceMap
import edu.stanford.nlp.ling.Label

/**
 * @author "Vic Nightfall"
 */
class Spell extends Action {
    
}

object SpellFactory extends TokenFactory[Spell] {
    override def createImpl(context: Context) : Spell = {
        new Spell().apply(context)
    }
}