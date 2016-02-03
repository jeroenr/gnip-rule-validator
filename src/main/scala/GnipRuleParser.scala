import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {
  private def doubleQuote = """\"""".r ^^ { _.toString }
  private def keyword = """[\w!?@#&%]+""".r ^^ { _.toString }
  private def optionallyNegatedKeyword = ("""-?""".r ^^ { _.toString }) ~ keyword

  private def recNonNegatedKeywords = rep1(keyword)
  private def recOptionallyNegatedKeywords = optionallyNegatedKeyword ~ rep(optionallyNegatedKeyword)

  private def quotedKeywords = doubleQuote ~ recNonNegatedKeywords ~ doubleQuote
  private def recQuotedKeywords = quotedKeywords ~ rep(quotedKeywords)

  private def quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords
  private def recKeywords = keyword ||| quotedKeywords ||| ((optionallyNegatedKeyword ||| quotedKeywords) ~ rep1(quotedOrUnquotedKeywords))

  def apply(rule: String) = parse(phrase(recKeywords), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
