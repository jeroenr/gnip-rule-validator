import fastparse.WhitespaceApi

import scala.io.Source
import scala.language.postfixOps

/**
 * Created by jero on 3-2-16.
 */
object GnipRuleValidator {
  val White = WhitespaceApi.Wrapper {
    import fastparse.all._
    NoTrace(" ".rep)
  }
  import fastparse.noApi._
  import White._

  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val stopWord = P(StringIn(STOP_WORDS: _*).!)
  private val wordChar = P(CharIn('a' to 'z') | CharIn('A' to 'Z'))
  private val numbers = P(CharIn('0' to '9'))
  private val operatorParam = P(":" ~~ wordChar.repX(min = 1).!)
  private val specialChar = P(CharIn("!%&\\'*+-./;<=>?,#@"))
  private val operators = OPERATORS.map(_ ~~ operatorParam).reduceLeft(_ | _)

  private val keyword = P((!"OR" ~ CharIn("#@").? ~~ wordChar ~~ (wordChar | specialChar).repX | operators).!)

  private val maybeNegatedKeyword = P(("-".? ~~ keyword).!)

  private val quotedKeyword = P(("\"".! ~ maybeNegatedKeyword.rep(min = 1) ~ "\"" ~~ ("~" ~~ numbers).?).!)

  private val keywordGroupWithoutOrClause = P((maybeNegatedKeyword | ("-".? ~~ quotedKeyword) | ("-".? ~~ keywordsInParentheses)).!)
  private val keywordGroup = P((keywordGroupWithoutOrClause | orClause).!)

  private def keywordsInParentheses = P(("(".! ~ gnipKeywordPhrase ~ ")").!)
  private def orClause = P((keywordGroupWithoutOrClause ~ "OR".! ~ !"-" ~~ gnipKeywordPhrase).!)

  private def gnipKeywordPhrase: Parser[String] = P(keywordGroup.rep(min = 1).!)

  private def notOnly(p: Parser[String]) = P(!(p.rep(min = 1) ~ End))

  private def guards = notOnly(stopWord) ~ notOnly("-" ~~ quotedKeyword) ~ notOnly("-" ~~ keyword) ~ notOnly("-" ~~ keywordsInParentheses)

  def apply(rule: String) = P(guards ~ gnipKeywordPhrase).parse(rule) match {
    case Parsed.Success(matched, index) => scala.util.Success(matched)
    case Parsed.Failure(lastParser, index, extra) =>
      println(extra.traced.trace)
      scala.util.Failure(new RuntimeException(extra.traced.trace))
  }
}
