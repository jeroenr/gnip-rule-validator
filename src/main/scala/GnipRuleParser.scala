import java.io

import scala.io.Source
import scala.util.parsing.combinator._
import scala.language.postfixOps

/**
 * Created by jero on 3-2-16.
 */

/**
 * Created by jero on 26-1-16.
 */
object GnipRuleParser extends RegexParsers {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val keyword = """[\w#@][\w!%&\\'*+-\./;<=>?,#@]*""".r ||| OPERATORS.map(_ ~ opt(""":[\w]+""".r)).reduceLeft(_ ||| _)
  private val maybeNegatedKeyword = opt("-") ~ keyword

  private val quotedKeyword = "\"" ~ (maybeNegatedKeyword+) ~ "\"" ~ opt("~[0-9]".r)

  private val maybeQuotedKeyword = maybeNegatedKeyword ||| quotedKeyword
  private val maybeQuotedKeywords = maybeQuotedKeyword+

  private val stopWord = STOP_WORDS.map(acceptSeq(_)).reduceLeft(_ | _)

  private val singleKeyword = not(stopWord) ~> (keyword ||| quotedKeyword) // FIXME: how to do logical AND properly
  private val multipleKeywords = maybeQuotedKeyword ~ maybeQuotedKeywords+

  private def keywordInParentheses = opt("-") ~ "(" ~ gnipKeywordPhrase ~ ")"

  private def gnipKeywordPhrase: GnipRuleParser.Parser[_] = (singleKeyword ||| multipleKeywords ||| keywordInParentheses)+

  def apply(rule: String) = parse(phrase(gnipKeywordPhrase), rule) match {
    case Success(matched, x) => scala.util.Success(matched)
    case NoSuccess(msg, x) => scala.util.Failure(new RuntimeException(msg))
  }
}
