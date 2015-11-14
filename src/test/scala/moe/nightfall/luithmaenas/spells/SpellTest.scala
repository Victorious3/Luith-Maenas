package moe.nightfall.luithmaenas.spells

import org.junit.Test
import moe.nightfall.luithmaenas.NLP
import org.junit.Before
import org.junit.Assert._
import edu.stanford.nlp.ling.LabelFactory
import edu.stanford.nlp.ling.Label

/**
 * @author "Vic Nightfall"
 */
class SpellTest {
    
    @Before def before() {
        NLP.init()
    }
    
    @Test def testNLP() {
        val input = "Create a shield when my health drops below 10 percent"
        
        NLP.annotate(input).sentences.foreach(println)
    }
}