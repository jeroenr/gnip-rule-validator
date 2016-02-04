import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleValidator extends RegexParsers {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val stopWord = STOP_WORDS.map(literal).reduceLeft(_ ||| _)
  private val operators = OPERATORS.map(_ ~ (""":[\w]+""".r?)).reduceLeft(_ ||| _)

  private val keyword = """[\w#@][\w!%&\\'*+-\./;<=>?,#@]*""".r ||| operators
  private val maybeNegatedKeyword = ("-"?) ~ keyword

  private val quotedKeyword = "\"" ~ (maybeNegatedKeyword +) ~ "\"" ~ ("~[0-9]".r ?)
  private val maybeNegatedQuotedKeyword = ("-"?) ~ quotedKeyword

  private val maybeQuotedKeyword = maybeNegatedKeyword ||| maybeNegatedQuotedKeyword

  private def keywordsInParentheses = "(" ~ gnipKeywordPhrase ~ ")"
  private def maybeNegatedKeywordsInParentheses = ("-"?) ~ keywordsInParentheses

  private def gnipKeywordPhrase: GnipRuleValidator.Parser[_] = (keyword ||| maybeQuotedKeyword ||| maybeNegatedKeywordsInParentheses)+

  private def guards = not(phrase(stopWord+)) ~ not(("-" ~ quotedKeyword)+) ~ not(("-" ~ keyword)+) ~ not(("-" ~ keywordsInParentheses)+)

  def apply(rule: String) = parse(phrase(guards ~ gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
