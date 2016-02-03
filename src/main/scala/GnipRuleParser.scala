import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {

  //  private def stopWords = """a|an|and|at|but|by|com|from|http|https|if|in|is|it|its|me|my|or|rt|the|this|to|too|via|we|www|you""".r ^^ { _.toString }
  private def doubleQuote = """\"""".r ^^ { _.toString }
  private def keyword = """[\w#][\w!%&\\'*+-\./;<=>?,#@]*""".r ^^ { _.toString }
  private def optionallyNegatedKeyword = ("""-?""".r ^^ { _.toString }) ~ keyword

  private def recOptionallyNegatedKeywords = rep1(optionallyNegatedKeyword)

  private def quotedKeywords = doubleQuote ~ recOptionallyNegatedKeywords ~ doubleQuote
  private def recQuotedKeywords = rep1(quotedKeywords)

  private def quotedOrUnquotedKeywords = recOptionallyNegatedKeywords ||| recQuotedKeywords
  private def recKeywords = keyword ||| quotedKeywords ||| ((optionallyNegatedKeyword ||| quotedKeywords) ~ rep1(quotedOrUnquotedKeywords))

  def apply(rule: String) = parse(phrase(recKeywords), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
