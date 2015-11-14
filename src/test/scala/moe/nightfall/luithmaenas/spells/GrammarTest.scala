package moe.nightfall.luithmaenas.spells

import org.junit.Before
import org.junit.Test

/**
 * @author "Vic Nightfall"
 */
class GrammarTest {
    @Before def before() {
        Grammar.init()
    }
    
    @Test def testGrammar() {
        Grammar.factory('NN).get
    }
}