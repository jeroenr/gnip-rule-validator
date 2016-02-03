import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {

  val STOPWORDS = Seq("a", "an", "and", "at", "but", "by", "com", "from", "http", "https", "if", "in", "is", "it", "its", "me", "my", "or", "rt", "the", "this", "to", "too", "via", "we", "www", "you")

  private val doubleQuote = """\"""".r ^^ { _.toString }
  private val keyword = """[\w#][\w!%&\\'*+-\./;<=>?,#@]*""".r ^^ { _.toString }
  private val optionallyNegatedKeyword = ("""-?""".r ^^ { _.toString }) ~ keyword

  private val recOptionallyNegatedKeywords = rep1(optionallyNegatedKeyword)

  private val quotedKeywords = doubleQuote ~ recOptionallyNegatedKeywords ~ doubleQuote
  private val recQuotedKeywords = rep1(quotedKeywords)

  private val quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords

  private val stopword = STOPWORDS.map(acceptSeq(_)).reduceLeft((a, b) => a | b)
  private val noStopWord = not(stopword)

  private val recKeywords = (noStopWord ~> (keyword ||| quotedKeywords)) ||| ((optionallyNegatedKeyword ||| quotedKeywords) ~ rep1(quotedOrUnquotedKeywords))

  def apply(rule: String) = parse(phrase(recKeywords), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
