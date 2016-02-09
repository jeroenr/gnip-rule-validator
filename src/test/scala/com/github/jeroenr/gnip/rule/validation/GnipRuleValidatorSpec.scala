package com.github.jeroenr.gnip.rule.validation

import org.scalatest._

class GnipRuleValidatorSpec extends WordSpec with MustMatchers with TryValues {
  "Gnip rule parser" should {
    "accept single character" in {
      GnipRuleValidator("h", "twitter").success
    }
    "NOT accept single #" in {
      GnipRuleValidator("#", "twitter").failure
    }
    "NOT accept single @" in {
      GnipRuleValidator("@", "twitter").failure
    }
    "NOT accept single @ in phrase" in {
      GnipRuleValidator("hello @ world", "twitter").failure
    }
    "NOT accept single @ in phrase 2" in {
      GnipRuleValidator("@", "twitter").failure
    }
    "NOT accept single + in phrase" in {
      GnipRuleValidator("hello + world", "twitter").failure
    }
    "NOT accept single special character" in {
      GnipRuleValidator("+", "twitter").failure
      GnipRuleValidator("!", "twitter").failure
      GnipRuleValidator("%", "twitter").failure
      GnipRuleValidator("&", "twitter").failure
      GnipRuleValidator("\\", "twitter").failure
      GnipRuleValidator("'", "twitter").failure
      GnipRuleValidator("*", "twitter").failure
      GnipRuleValidator("-", "twitter").failure
      GnipRuleValidator(".", "twitter").failure
      GnipRuleValidator("/", "twitter").failure
      GnipRuleValidator(";", "twitter").failure
      GnipRuleValidator("<", "twitter").failure
      GnipRuleValidator("=", "twitter").failure
      GnipRuleValidator(">", "twitter").failure
      GnipRuleValidator("?", "twitter").failure
      GnipRuleValidator(",", "twitter").failure
    }
    "accept single word" in {
      GnipRuleValidator("hello", "twitter").success
    }
    "accept upcasing word" in {
      GnipRuleValidator("helLo WOrlD", "twitter").success
    }
    "accept two words" in {
      GnipRuleValidator("hello world", "twitter").success
    }
    "accept special characters in word" in {
      GnipRuleValidator("hello!%&\'*+-./;<=>?,#@world", "twitter").success
    }
    "accept single hashtag" in {
      GnipRuleValidator("#yolo", "twitter").success
    }
    "not accept special characters in beginning of word" in {
      GnipRuleValidator("!hello", "twitter").failure
    }
    "accept multiple words" in {
      GnipRuleValidator("hello? beautiful world!", "twitter").success
    }
    "accept multiple words with negation" in {
      GnipRuleValidator("hello? -beautiful world! -foo -bar bla -lol", "twitter").success
    }
    "accept quoted word" in {
      GnipRuleValidator("\"hello\"", "twitter").success
    }
    "NOT accept wrong quotes" in {
      GnipRuleValidator("'hello'", "twitter").failure
      GnipRuleValidator("''hello''", "twitter").failure
    }
    "accept quoted words" in {
      GnipRuleValidator("\"hello world!\"", "twitter").success
    }
    "accept quoted negated words" in {
      GnipRuleValidator("\"hello -world!\"", "twitter").success
    }
    "accept negated quoted words" in {
      GnipRuleValidator("bla -\"hello world!\"", "twitter").success
    }
    "NOT accept negated quoted words in start position" in {
      GnipRuleValidator("-\"hello world!\"", "twitter").failure
    }
    "NOT accept only negated quoted words" in {
      GnipRuleValidator("-\"hello world!\" -\"bye world!\"", "twitter").failure
    }
    "NOT accept spaces between negation" in {
      GnipRuleValidator("hello - world", "twitter").failure
    }
    "accept all combinations of optional negation and quoted words" in {
      GnipRuleValidator("\"hello world?\" bla -bla \"lol!\" bla", "twitter").success
    }
    "NOT accept single negated word" in {
      GnipRuleValidator("-hello", "twitter").failure
    }
    "NOT accept only negated words" in {
      GnipRuleValidator("-hello -world", "twitter").failure
    }
    "NOT accept unfinished quotes" in {
      GnipRuleValidator("\"-hello world\" bla \"lol bla bla", "twitter").failure
    }
    "NOT accept empty string" in {
      GnipRuleValidator("", "twitter").failure
    }
    "NOT accept single stop word" in {
      GnipRuleValidator("the", "twitter").failure
    }
    "NOT accept single stop word 2" in {
      GnipRuleValidator("at", "twitter").failure
    }
    "accept stop word combined with non stop word" in {
      GnipRuleValidator("the boat", "twitter").success
    }
    "NOT accept only stop words" in {
      GnipRuleValidator("a an and at but by com from http https if in is it its me my or rt the this to too via we www you", "twitter").failure
    }
    "NOT accept only stop words 2" in {
      GnipRuleValidator("a an", "twitter").failure
    }
    "accept group" in {
      GnipRuleValidator("(the boat)", "twitter").success
    }
    "accept groups" in {
      GnipRuleValidator("(the boat) (the other boat)", "twitter").success
    }
    "accept nested groups" in {
      GnipRuleValidator("((bla bla))", "twitter").success
    }
    "accept nested groups with terms before" in {
      GnipRuleValidator("(the boat (bla bla))", "twitter").success
    }
    "accept nested groups with terms after" in {
      GnipRuleValidator("((bla bla) lol lol)", "twitter").success
    }
    "accept nested groups with terms before AND after" in {
      GnipRuleValidator("(lol lol (\"bla\" bla) -lol lol)", "twitter").success
    }
    "accept groups combined with non-groups" in {
      GnipRuleValidator("the boat (bla bla)", "twitter").success
    }
    "accept negated groups" in {
      GnipRuleValidator("the boat -(bla bla)", "twitter").success
    }
    "NOT accept negated group in start position" in {
      GnipRuleValidator("-(bla bla)", "twitter").failure
    }
    "NOT accept only negated groups" in {
      GnipRuleValidator("-(bla bla) -(lol lol)", "twitter").failure
    }
    "accept quoted keywords in groups" in {
      GnipRuleValidator("(\"bla\" \"bla\")", "twitter").success
    }
    "NOT accept unclosed groups" in {
      GnipRuleValidator("(hello (world) bla", "twitter").failure
    }
    "accept single powertrack operator" in {
      GnipRuleValidator("lang:EN", "twitter").success
    }
    "NOT accept invalid use of powertrack operator" in {
      GnipRuleValidator("lang:", "twitter").failure
    }
    "accept proximity operator" in {
      GnipRuleValidator("\"happy birthday\"~3", "twitter").success
    }
    "accept powertrack operator with terms before" in {
      GnipRuleValidator("bla lang:en", "twitter").success
    }
    "accept powertrack operator with terms after" in {
      GnipRuleValidator("lang:en bla", "twitter").success
    }
    "accept powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla lang:en bla", "twitter").success
    }
    "accept powertrack operator with terms in parentheses before" in {
      GnipRuleValidator("(bla bla) lang:en", "twitter").success
    }
    "accept powertrack operator with terms in parentheses after" in {
      GnipRuleValidator("lang:en (bla bla)", "twitter").success
    }
    "accept powertrack operator with terms in parentheses before AND after" in {
      GnipRuleValidator("(bla bla) lang:en (bla bla)", "twitter").success
    }
    "accept negated powertrack operator with terms after" in {
      GnipRuleValidator("-lang:en bla", "twitter").success
    }
    "accept negated powertrack operator with terms before" in {
      GnipRuleValidator("bla -lang:en", "twitter").success
    }
    "NOT accept only negated powertrack operators" in {
      GnipRuleValidator("-lang:en -contains:lol", "twitter").failure
    }
    "accept negated powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla -lang:en bla", "twitter").success
    }
    "accept multiple powertrack operators" in {
      GnipRuleValidator("lang:en has:links from:8744 contains:help url_contains:foo", "twitter").success
    }
    "NOT accept OR missing terms after" in {
      GnipRuleValidator("this OR", "twitter").failure
    }
    "NOT accept OR in group missing terms after" in {
      GnipRuleValidator("(gnip OR)", "twitter").failure
    }
    "NOT accept multiple OR missing terms after" in {
      GnipRuleValidator("this OR that OR", "twitter").failure
    }
    "NOT accept OR missing terms before" in {
      GnipRuleValidator("OR bla", "twitter").failure
    }
    "NOT accept negated OR right" in {
      GnipRuleValidator("these OR -that", "twitter").failure
    }
    "NOT accept negated OR left" in {
      GnipRuleValidator("-these OR that", "twitter").failure
    }
    "accept negated OR followed by positive term(s)" in {
      GnipRuleValidator("these OR -that bla OR these", "twitter").success
    }
    "accept OR" in {
      GnipRuleValidator("these OR that", "twitter").success
    }
    "NOT accept OR with only stop words left clause" in {
      GnipRuleValidator("this OR that", "twitter").failure
    }
    "NOT accept OR with only stop words right clause" in {
      GnipRuleValidator("that OR this", "twitter").failure
    }
    "accept ORbit" in {
      GnipRuleValidator("ORbit", "twitter").success
    }
    "accept multiple OR" in {
      GnipRuleValidator("these OR that OR these", "twitter").success
    }
    "accept OR between groups" in {
      GnipRuleValidator("(these that) OR (that these)", "twitter").success
    }
    "accept OR between group and keyword" in {
      GnipRuleValidator("(these that) OR that", "twitter").success
    }
    "accept OR between keyword and group" in {
      GnipRuleValidator("that OR (these that)", "twitter").success
    }
    "accept OR between quotes" in {
      GnipRuleValidator("\"the boat\" OR \"that boat\"", "twitter").success
    }
    "accept OR between quote and keyword" in {
      GnipRuleValidator("\"the boat\" OR that", "twitter").success
    }
    "accept OR between keyword and quote" in {
      GnipRuleValidator("that OR \"the boat\"", "twitter").success
    }
    "accept bounding box operator" in {
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505 40.09455]", "twitter").success
    }
    "NOT accept wrong use of bounding box operator" in {
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505]", "twitter").failure
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505 45 4]", "twitter").failure
      GnipRuleValidator("profile_bounding_box:[-105.301758 -105.178505]", "twitter").failure
    }
    "NOT accept special ops for normal ops operator" in {
      GnipRuleValidator("country_code:[-105.301758 39.964069 -105.178505]", "twitter").failure
    }
    "accept profile_point_radius operator" in {
      GnipRuleValidator("profile_point_radius:[-105.27346517 40.01924738 10km]", "twitter").success
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 10.2mi]", "twitter").success
    }
    "NOT accept wrong use of profile_point_radius operator" in {
      GnipRuleValidator("profile_point_radius:[-105.27346517 40.01924738 10 km]", "twitter").failure
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 10.2m]", "twitter").failure
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 40.01924738 10.2mi]", "twitter").failure
      GnipRuleValidator("profile_point_radius:[105.27346517 10.2mi]", "twitter").failure
    }
    "accept statuses_count operator" in {
      GnipRuleValidator("statuses_count:1000..10000", "twitter").success
      GnipRuleValidator("statuses_count:1000", "twitter").success
    }
    "accept klout_score operator" in {
      GnipRuleValidator("klout_score:1000..10000", "twitter").success
      GnipRuleValidator("klout_score:1000", "twitter").success
    }
    "NOT accept wrong use of statuses_count operator" in {
      GnipRuleValidator("statuses_count:1000...10000", "twitter").failure
      GnipRuleValidator("statuses_count:1000.0", "twitter").failure
      GnipRuleValidator("statuses_count:-900", "twitter").failure
    }
    "accept quoted keywords as operator param" in {
      GnipRuleValidator("profile_subregion:\"San Francisco County\"", "twitter").success
    }
    "accept full syntax" in {
      GnipRuleValidator("(gnip OR from:688583 OR @gnip OR datasift) (\"powertrack -operators\" OR (-\"streaming code\"~4 foo OR bar)) -contains:help has:links url_contains:github", "twitter").success
    }
    "accept gender operator for Foursquare only" in {
      GnipRuleValidator("gender:male", "twitter").failure
      GnipRuleValidator("gender:male", "foursquare").success
    }
  }

}
