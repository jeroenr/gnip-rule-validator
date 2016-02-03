import scala.io.Source
import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {
  val STOPWORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines().toSeq

  private val doubleQuote = """\"""".r ^^ { _.toString }
  private val keyword = """[\w#][\w!%&\\'*+-\./;<=>?,#@]*""".r ^^ { _.toString }
  private val optionallyNegatedKeyword = ("""-?""".r ^^ { _.toString }) ~ keyword

  private val recOptionallyNegatedKeywords = rep1(optionallyNegatedKeyword)

  private val quotedKeywords = doubleQuote ~ recOptionallyNegatedKeywords ~ doubleQuote
  private val recQuotedKeywords = rep1(quotedKeywords)

  private val quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords

  private val stopword = STOPWORDS.map(acceptSeq(_)).reduceLeft((a, b) => a | b)
  private val noStopWord = not(stopword)

  private val singleKeyword = noStopWord ~> (keyword ||| quotedKeywords)
  private val multipleKeywords = (optionallyNegatedKeyword ||| quotedKeywords) ~ rep1(quotedOrUnquotedKeywords)

  private val gnipKeywordPhrase = phrase(singleKeyword ||| multipleKeywords)

  def apply(rule: String) = parse(gnipKeywordPhrase, rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
