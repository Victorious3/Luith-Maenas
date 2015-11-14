package moe.nightfall.luithmaenas.spells

import edu.stanford.nlp.trees.Tree

/**
 * @author "Vic Nightfall"
 */
object Spell {
    def create(spell: String) : Spell = null
}

class Spell(tree: Tree) extends Action('spell, tree) {
    
}