import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator._
import fastparse.all._

/**
 * Created by jero on 3-2-16.
 */
object GnipRuleValidator {
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val stopWord = P(StringIn(STOP_WORDS: _*).!)
  private val wordChar = CharIn('a' to 'z') | CharIn('A' to 'Z')
  private val numbers = CharIn('0' to '9')
  private val operatorParam = P(":" ~ wordChar.rep(min = 1).!)
  private val specialChar = CharIn("!%&\\'*+-./;<=>?,#@")
  private val operators = OPERATORS.map(_ ~ operatorParam).reduceLeft(_ | _)

  private val keyword = P((!"OR" ~ CharIn("#@").? ~ wordChar ~ (wordChar | specialChar).rep | operators).!)

  private val maybeNegatedKeyword = P(("-".? ~ keyword).!)

  private val quotedKeyword = P(("\"".! ~ maybeNegatedKeyword.rep(min = 1) ~ "\"" ~ ("~" ~ numbers).?).!)

  private val keywordGroupWithoutOrClause = P((maybeNegatedKeyword | ("-".? ~ quotedKeyword) | ("-".? ~ keywordsInParentheses)).!)
  private val keywordGroup = P((keywordGroupWithoutOrClause | orClause).!)

  private def keywordsInParentheses = P(("(".! ~ gnipKeywordPhrase ~ ")").!)
  private def orClause = P((keywordGroupWithoutOrClause ~ "OR".! ~ !"-" ~ gnipKeywordPhrase).!)

  private def gnipKeywordPhrase: all.Parser[String] = P(keywordGroup.rep(min = 1).!)

  private def notOnly(p: all.Parser[String]) = P(!(p.rep(min = 1) ~ End))

  //private def guards = notOnly(stopWord) ~ notOnly(P("-".! ~ quotedKeyword)) ~ notOnly("-" ~ keyword) ~ notOnly("-" ~ keywordsInParentheses)

  def apply(rule: String) = gnipKeywordPhrase.parse(rule) match {
    case Parsed.Success(matched, x) => scala.util.Success(matched)
    case Parsed.Failure(msg, x, extra) => scala.util.Failure(new RuntimeException(extra.traced.trace))
  }
}
