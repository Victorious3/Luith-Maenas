package moe.nightfall.luithmaenas

import moe.nightfall.luithmaenas.spells.token.Token
import moe.nightfall.luithmaenas.spells.Spell
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.Properties
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.util.CoreMap
import scala.collection.JavaConverters
import scala.collection.JavaConversions
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.semgraph.SemanticGraph
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation
import edu.stanford.nlp.ling.Label
import edu.stanford.nlp.ling.IndexedWord

/**
 * @author "Vic Nightfall"
 */
object NLP {
    
    val annotators = Seq("tokenize", "ssplit", "pos", "lemma", "ner", "parse", "dcoref")
    
    private var _pipeline: Option[StanfordCoreNLP] = None
    private var loadingStatus = "uninitialized"
    
    /** Can be used to monitor the loading status */
    def status = loadingStatus
    
    def pipeline = {if (!isInitialized) init(); _pipeline.get}
    
    def isInitialized = !_pipeline.isEmpty
    
    private def loadAnnotator(nlp: StanfordCoreNLP, annotator: String) {
        loadingStatus = annotator
        nlp.addAnnotator(StanfordCoreNLP.getExistingAnnotator(annotator))
    }
    
    def init() : Unit = this.synchronized {
        // Check in order to avoid initializing twice, for concurrency reasons
        if (isInitialized) return
        
        // Need to pass some empty properties here or else its going to initialize with some defaults...
        val properties = new Properties()
        properties.setProperty("annotators", "")
        val nlp = new StanfordCoreNLP(properties, false) 
        annotators.foreach(loadAnnotator(nlp, _))
        loadingStatus = "initialized"
        
        _pipeline = Some(nlp)
    }
    
    def annotate(content: String) : Document = {
        return annotate(new Document(content))
    }
    
    def annotate(doc: Document) : Document = {
        pipeline.annotate(doc.annotation)
        return doc
    }
}

/** Contains Symbols to match POS- Labels */
object POS {
    implicit class Category(val symbol: Symbol) {
       final override def equals(other: Any) : Boolean = {
           if (other == this) return true
           val symbol = other match {
               case other: Symbol => other
               case other: Category => other.symbol
               case other: Label => Symbol(other.value)
               case _ => return false
           }
           return equals(symbol)
       }
       
       final override def hashCode : Int = symbol.hashCode
       
       def equals(other: Symbol) : Boolean = {
           return symbol == this.symbol || symbol.name.contains(this.symbol.name)
       }
    }
    
    implicit def toCategory(label: Label) : Category = Category(Symbol(label.value))
    implicit def toSymbol(label: Label) : Symbol = Symbol(label.value)
    
    val coordinating_conjunction = Category('CC)
    val cardinal_number          = Category('CN)
    val determiner               = Category('DT)
    val existential_there        = Category('ET)
    val foregin_word             = Category('FW)
    val preposition              = Category('IN)
    
    object adjective extends Category('JJ) {
        val comparative          = Category('JJC)
        val superlative          = Category('JJS)
    }
    
    val list                     = Category('LS)
    val modal                    = Category('MD)
    
    object noun extends Category('NN) {
        val strict_plural = Category('NNS)
        
        /** Used to match both NNS and NNPS */
        object plural extends Category('NNS) {
            override def equals(other: Symbol) = super.equals(other) || other == noun.proper.plural
        }
        
        object proper extends Category('NNP) {
            val plural = Category('NNPS)
        }
    }
    
    val predeterminer     = Category('PDT)
    val possesive_ending  = Category('POS)
    val personal_pronoun  = Category('PRP)
    val possesive_pronoun = Category('PRP$)
    
    object adverb extends Category('RB) {
        val comperative   = Category('RBC)
        val superlative   = Category('RBS)
    }
    
    val particle          = Category('RP)
    val symbol            = Category('SYM)
    val to                = Category('TO)
    val interjection      = Category('UH)
    
    object verb extends Category('VB) {
        val past_tense         = Category('VBP)
        val gerund             = Category('VBG)
        val past_participle    = Category('VBN)
        val non_third_singular = Category('VBP)
        val third_singular     = Category('VBZ)
    }
    
    val wh_determiner    = Category('WDT)
    
    object wh_pronoun extends Category('WP) {
        val possesive    = Category('WP$)
    }
    
    val wh_adverb        = Category('WRB)
}

class Document(content: String) {
    val annotation = new Annotation(content)
    
    lazy val sentences: Seq[SentenceMap] = 
        JavaConversions.asScalaBuffer(annotation.get(classOf[SentencesAnnotation])).map(new SentenceMap(_))
}

class SentenceMap(content: CoreMap) {
    lazy val tree: Tree = content.get(classOf[TreeAnnotation])
    lazy val dependencies: SemanticGraph = content.get(classOf[CollapsedCCProcessedDependenciesAnnotation])
}