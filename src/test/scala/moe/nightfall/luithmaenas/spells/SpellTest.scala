package moe.nightfall.luithmaenas.spells

import org.junit.Test
import moe.nightfall.luithmaenas.NLP
import org.junit.Before
import org.junit.Assert._
import edu.stanford.nlp.ling.LabelFactory
import edu.stanford.nlp.ling.Label
import scala.collection.JavaConversions

/**
 * @author "Vic Nightfall"
 */
class SpellTest {
    
    @Before def before() {
        NLP.init()
    }
    
    @Test def testNLP() {
        val input = "Create a shield when my health drops below 10 percent"
        val document = NLP.annotate(input)
        
        document.sentences.foreach { sentence =>
            sentence.tree.pennPrint()
            println()
            JavaConversions.asScalaIterator(sentence.dependencies.edgeIterable().iterator()).foreach(println)
        }
    }
}