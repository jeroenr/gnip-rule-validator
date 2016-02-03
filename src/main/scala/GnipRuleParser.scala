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

  private val keyword = """[\w#][\w!%&\\'*+-\./;<=>?,#@]*""".r ^^ { _.toString }
  private val optionallyNegatedKeyword = ("""-?""".r ^^ { _.toString }) ~ keyword

  private val recOptionallyNegatedKeywords = rep1(optionallyNegatedKeyword)

  private val quotedKeywords = "\"" ~ recOptionallyNegatedKeywords ~ "\""
  private val recQuotedKeywords = rep1(quotedKeywords)

  private val quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords

  private val stopword = STOPWORDS.map(acceptSeq(_)).reduceLeft((a, b) => a | b)
  private val noStopWord = not(stopword)

  private val singleKeyword = noStopWord ~> (keyword ||| quotedKeywords)
  private val multipleKeywords = (optionallyNegatedKeyword ||| quotedKeywords) ~ rep1(quotedOrUnquotedKeywords)

  private def keywordInParentheses = "(" ~ gnipKeywordPhrase ~ ")"

  private def gnipKeywordPhrase: GnipRuleParser.Parser[_] = singleKeyword ||| rep1(multipleKeywords ||| keywordInParentheses)

  def apply(rule: String) = parse(phrase(gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
