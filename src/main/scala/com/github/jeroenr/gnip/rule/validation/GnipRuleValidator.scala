package com.github.jeroenr.gnip.rule.validation

import fastparse.WhitespaceApi
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.language.postfixOps

/**
 * Created by jero on 3-2-16.
 */
class GnipRuleParser(source: String) {
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
  val OPERATORS = Source.fromInputStream(getClass.getResourceAsStream(s"/operators/$source")).getLines.toSeq
  val STOP_WORDS = Source.fromInputStream(getClass.getResourceAsStream("/stopwords")).getLines.toSeq
  val LANG_CODES = Source.fromInputStream(getClass.getResourceAsStream("/lang_codes")).getLines.toSeq
  val COUNTRY_CODES = Source.fromInputStream(getClass.getResourceAsStream("/country_codes")).getLines.toSeq

  private val stopWord = P(StringIn(STOP_WORDS: _*)!)
  private val number = P(CharIn('0' to '9'))
  private val wordChar = P(CharIn('a' to 'z') | CharIn('A' to 'Z') | number | "_")
  private val quotedWord = P(("\"" ~ (wordChar+) ~ "\"")!)
  private val digit = P((number++) ~ (("." ~~ (number ++))?))
  private val latOrLon = P(("-"?) ~~ digit)

  // TODO: make configurable (read from file or something)
  private val numberRangeOps = Seq("statuses_count", "klout_score", "friends_count", "followers_count", "listed_count")
  private val numericOps = Seq("sample")
  private val boundingBoxOps = Seq("bounding_box", "profile_bounding_box")
  private val pointRadiusOps = Seq("point_radius", "profile_point_radius")
  private val genderOps = Seq("gender")
  private val langOps = Seq("lang", "twitter_lang")
  private val countryOps = Seq("country_code", "profile_country_code")

  private val boundingBox = P((boundingBoxOps.map(P(_)).reduceLeft(_ | _) ~~ ":[" ~ latOrLon.rep(min = 4, max = 4) ~ "]")!)
  private val pointRadius = P((pointRadiusOps.map(P(_)).reduceLeft(_ | _) ~~ ":[" ~ latOrLon.rep(min = 2, max = 2) ~ digit ~~ ("mi" | "km") ~ "]")!)
  private val numberRange = P((numberRangeOps.map(P(_)).reduceLeft(_ | _) ~~ ":" ~~ ((number++) ~~ ((".." ~~ (number++))?)))!)
  private val numericOp = P((numericOps.map(P(_)).reduceLeft(_ | _) ~~ ":" ~~ (number++))!)
  private val genderOp = P((genderOps.map(P(_)).reduceLeft(_ | _) ~~ ":" ~~ ("male" | "female") ~~ (!wordChar))!)
  private val langOp = P((langOps.map(P(_)).reduceLeft(_ | _) ~~ ":" ~~ LANG_CODES.map(lc => P(IgnoreCase(lc) ~~ !wordChar)).reduceLeft(_ | _))!)
  private val countryOp = P((countryOps.map(P(_)).reduceLeft(_ | _) ~~ ":" ~~ COUNTRY_CODES.map(lc => P(IgnoreCase(lc) ~~ !wordChar)).reduceLeft(_ | _))!)

  // TODO: restrict country and lang operators
  private val specialOps = (
    numberRangeOps.map(_ -> numberRange) ++
    numericOps.map(_ -> numericOp) ++
    boundingBoxOps.map(_ -> boundingBox) ++
    pointRadiusOps.map(_ -> pointRadius) ++
    genderOps.map(_ -> genderOp) ++
    langOps.map(_ -> langOp) ++
    countryOps.map(_ -> countryOp)
  ).toMap

  private val operatorParam = P(":" ~~ (quotedWord | (wordChar++)))
  private val specialChar = P(CharIn("!%&\\'*+-./;<=>?,#@"))
  private val operators = P(OPERATORS.map { op =>
    specialOps.getOrElse(op, op ~~ (operatorParam?))
  }.reduceLeft(_ | _))

  private val keyword = P((operators | ((CharIn("#@")?) ~~ wordChar ~~ ((wordChar | specialChar)**)))!).filter(_ != "OR")
  private val maybeNegatedKeyword = P((("-"?) ~~ keyword)!)

  private val quotedKeyword = P(("\"" ~ (maybeNegatedKeyword+) ~ "\"" ~~ (("~" ~~ number)?))!)

  private val keywordGroupWithoutOrClause = P((("-"?) ~~ quotedKeyword) | maybeNegatedKeyword | (("-"?) ~~ keywordsInParentheses))
  private val keywordGroup = P(orClause | keywordGroupWithoutOrClause).opaque("<keyword-group>")

  private def keywordsInParentheses = P("(" ~ gnipKeywordPhrase ~ ")")
  private def orClause = P(!(((stopWord+) | ("-" ~~ (keywordGroupWithoutOrClause+))) ~ "OR") ~ keywordGroupWithoutOrClause ~ "OR" ~ notOnly(stopWord) ~ gnipKeywordPhrase)
  private def notOnly(p: Parser[String]) = P(!((p+) ~ End))
  private def guards = notOnly(stopWord).opaque("NOT ONLY STOPWORDS") ~ (notOnly("-" ~~ quotedKeyword) ~ notOnly("-" ~~ keyword) ~ notOnly("-" ~~ keywordsInParentheses)).opaque("NOT ONLY NEGATED TERMS")

  private def gnipKeywordPhrase: Parser[String] = P(guards ~ (keywordGroup+)!).opaque("<phrase>")

  def parse(rule: String) = P(Start ~ gnipKeywordPhrase ~ End).parse(rule)

}

object GnipRuleValidator {
  import fastparse.core.Parsed._
  import fastparse.core.ParseError

  val log = LoggerFactory.getLogger(getClass)

  def apply(rule: String, source: String) = new GnipRuleParser(source).parse(rule) match {
    case Success(matched, index) =>
      log.debug(s"Matched: $matched")
      scala.util.Success(matched)
    case f @ Failure(lastParser, index, extra) => {
      val parseError = ParseError(f)
      log.warn(s"Failed to parse rule, expected '$lastParser'. Trace: ${parseError.getMessage}")
      scala.util.Failure(parseError)
    }
  }
}
