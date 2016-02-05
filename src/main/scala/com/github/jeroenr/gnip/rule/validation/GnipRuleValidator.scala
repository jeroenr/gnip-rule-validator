package com.github.jeroenr.gnip.rule.validation

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

  implicit class RichParser[T](p: Parser[T]) {
    def + = p.rep(min = 1)
    def ++ = p.repX(min = 1)
    def * = p.rep
    def ** = p.repX
  }

  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream("/operators")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq

  private val stopWord = P(StringIn(STOP_WORDS: _*)!)
  private val number = P(CharIn('0' to '9'))
  private val wordChar = P(CharIn('a' to 'z') | CharIn('A' to 'Z') | number | "_")
  private val operatorParam = P(":" ~~ (wordChar++))
  private val specialChar = P(CharIn("!%&\\'*+-./;<=>?,#@"))
  private val operators = P(OPERATORS.map(_ ~~ (operatorParam?)).reduceLeft(_ | _))

  private val keyword = P((operators | ((CharIn("#@")?) ~~ wordChar ~~ ((wordChar | specialChar)**)))!).filter(_ != "OR")
  private val maybeNegatedKeyword = P((("-"?) ~~ keyword)!)
  private val quotedKeyword = P(("\"" ~ (maybeNegatedKeyword+) ~ "\"" ~~ (("~" ~~ number)?))!)

  private val keywordGroupWithoutOrClause = P((("-"?) ~~ quotedKeyword) | maybeNegatedKeyword | (("-"?) ~~ keywordsInParentheses))
  private val keywordGroup = P(orClause | keywordGroupWithoutOrClause)

  private def keywordsInParentheses = P("(" ~ gnipKeywordPhrase ~ ")")
  private def orClause = P(keywordGroupWithoutOrClause ~ "OR" ~ !"-" ~ gnipKeywordPhrase)
  private def gnipKeywordPhrase: Parser[String] = P((keywordGroup+)!)

  private def notOnly(p: Parser[String]) = P(!((p+) ~ End))
  private def guards = notOnly(stopWord) ~ notOnly("-" ~~ quotedKeyword) ~ notOnly("-" ~~ keyword) ~ notOnly("-" ~~ keywordsInParentheses)

  def apply(rule: String) = P(Start ~ guards ~ gnipKeywordPhrase ~ End).parse(rule) match {
    case Parsed.Success(matched, index) => scala.util.Success(matched)
    case Parsed.Failure(lastParser, index, extra) =>
      println(s"traced: ${extra.traced.trace}")
      scala.util.Failure(new RuntimeException(extra.traced.trace))
  }
}
