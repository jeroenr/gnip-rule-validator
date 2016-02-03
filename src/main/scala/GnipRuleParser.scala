import scala.io.Source
import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOPWORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val keyword = """[\w#][\w!%&\\'*+-\./;<=>?,#@]*""".r ||| OPERATORS.map(op => (op + """:?[\w]*""").r ^^ { _.toString }).reduceLeft(_ ||| _)
  private val optionallyNegatedKeyword = opt("-") ~ keyword

  private val recOptionallyNegatedKeywords = optionallyNegatedKeyword+

  private val quotedKeywords = "\"" ~ recOptionallyNegatedKeywords ~ "\"" ~ opt("(~[0-9])".r)
  private val recQuotedKeywords = quotedKeywords+

  private val quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords

  private val stopword = STOPWORDS.map(acceptSeq(_)).reduceLeft(_ | _)
  private val noStopWord = not(stopword)

  private val singleKeyword = noStopWord ~> (keyword ||| quotedKeywords)
  private val multipleKeywords = (optionallyNegatedKeyword ||| quotedKeywords) ~ quotedOrUnquotedKeywords+

  private def keywordInParentheses = "(" ~ gnipKeywordPhrase ~ ")"

  private def gnipKeywordPhrase: GnipRuleParser.Parser[_] = singleKeyword ||| ((multipleKeywords ||| keywordInParentheses)+)

  def apply(rule: String) = parse(phrase(gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
