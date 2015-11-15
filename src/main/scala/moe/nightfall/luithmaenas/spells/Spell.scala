package moe.nightfall.luithmaenas.spells

import edu.stanford.nlp.trees.Tree

/**
 * @author "Vic Nightfall"
 */
class Spell extends Action {
    
}

object SpellFactory extends TokenFactory[Spell] {
    override def createImpl(tree: Tree, parent: Token) : Spell = {
        Token(() => new Spell, this, parent, tree)
    }
}