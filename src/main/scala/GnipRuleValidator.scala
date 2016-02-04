import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator._

/**
 * Created by jero on 3-2-16.
 */
object GnipRuleValidator extends RegexParsers {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val stopWord = STOP_WORDS.map(literal).reduceLeft(_ ||| _)
  private val operators = OPERATORS.map(_ ~ (""":[\w]+""".r?)).reduceLeft(_ ||| _)

  private val keyword = not("OR") ~ """[#@]?[\w][\w!%&\\'*+-\./;<=>?,#@]*""".r ||| operators
  private val maybeNegatedKeyword = ("-"?) ~ keyword
  private val quotedKeyword = "\"" ~ (maybeNegatedKeyword+) ~ "\"" ~ ("~[0-9]".r ?)

  private val keywordGroup = maybeNegatedKeyword ||| ("-"?) ~ quotedKeyword ||| ("-"?) ~ keywordsInParentheses

  private def keywordsInParentheses = "(" ~ gnipKeywordPhrase ~ ")"
  private def gnipKeywordPhrase: GnipRuleValidator.Parser[_] = keywordGroup+

  private def notOnly(p: GnipRuleValidator.Parser[_]) = not(phrase(p+))
  private def guards = notOnly(stopWord) ~ notOnly("-" ~ quotedKeyword) ~ notOnly("-" ~ keyword) ~ notOnly("-" ~ keywordsInParentheses)

  def apply(rule: String) = parse(phrase(guards ~ gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
