import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val keyword = """[\w#@][\w!%&\\'*+-\./;<=>?,#@]*""".r ||| OPERATORS.map(_ ~ opt(""":[\w]+""".r)).reduceLeft(_ | _)
  private val maybeNegatedKeyword = opt("-") ~ keyword
  private val negatedKeyword = "-" ~ keyword
  private val negatedKeywords = negatedKeyword+

  private val quotedKeyword = opt("-") ~ "\"" ~ (maybeNegatedKeyword+) ~ "\"" ~ opt("~[0-9]".r)
  private val negatedQuotedKeywords = "-" ~ quotedKeyword

  private val maybeQuotedKeyword = maybeNegatedKeyword ||| quotedKeyword
  private val maybeQuotedKeywords = maybeQuotedKeyword+

  private val stopWord = STOP_WORDS.map(acceptSeq(_)).reduceLeft(_ | _)

  private val singleKeyword = not(stopWord) ~> (keyword ||| quotedKeyword) // FIXME: how to do logical AND properly
  private val multipleKeywords = maybeQuotedKeyword ~ maybeQuotedKeywords+

  private def keywordsInParentheses = "(" ~ gnipKeywordPhrase ~ ")"
  private def maybeNegatedKeywordsInParentheses = opt("-") ~ keywordsInParentheses
  private def negatedInParentheses = ("-" ~ keywordsInParentheses)+

  private def gnipKeywordPhrase: GnipRuleParser.Parser[_] = (singleKeyword ||| multipleKeywords | maybeNegatedKeywordsInParentheses)+

  private def guards = guard(not(negatedQuotedKeywords)) ~ guard(not(negatedKeywords)) ~ guard(not(negatedInParentheses))

  def apply(rule: String) = parse(phrase(guards ~ gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
