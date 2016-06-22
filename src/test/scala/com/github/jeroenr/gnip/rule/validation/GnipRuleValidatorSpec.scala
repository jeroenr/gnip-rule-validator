package com.github.jeroenr.gnip.rule.validation

import org.scalatest._

class GnipRuleValidatorSpec extends WordSpec with MustMatchers with TryValues {
  "Gnip rule parser" should {
    "accept single character" in {
      GnipRuleValidator("h", Twitter, PowertrackV1).success
    }
    "NOT accept single #" in {
      GnipRuleValidator("#", Twitter, PowertrackV1).failure
    }
    "NOT accept single @" in {
      GnipRuleValidator("@", Twitter, PowertrackV1).failure
    }
    "NOT accept single @ in phrase" in {
      GnipRuleValidator("hello @ world", Twitter, PowertrackV1).failure
    }
    "NOT accept single @ in phrase 2" in {
      GnipRuleValidator("@", Twitter, PowertrackV1).failure
    }
    "NOT accept single + in phrase" in {
      GnipRuleValidator("hello + world", Twitter, PowertrackV1).failure
    }
    "NOT accept single special character" in {
      GnipRuleValidator("+", Twitter, PowertrackV1).failure
      GnipRuleValidator("!", Twitter, PowertrackV1).failure
      GnipRuleValidator("%", Twitter, PowertrackV1).failure
      GnipRuleValidator("&", Twitter, PowertrackV1).failure
      GnipRuleValidator("\\", Twitter, PowertrackV1).failure
      GnipRuleValidator("'", Twitter, PowertrackV1).failure
      GnipRuleValidator("*", Twitter, PowertrackV1).failure
      GnipRuleValidator("-", Twitter, PowertrackV1).failure
      GnipRuleValidator(".", Twitter, PowertrackV1).failure
      GnipRuleValidator("/", Twitter, PowertrackV1).failure
      GnipRuleValidator(";", Twitter, PowertrackV1).failure
      GnipRuleValidator("<", Twitter, PowertrackV1).failure
      GnipRuleValidator("=", Twitter, PowertrackV1).failure
      GnipRuleValidator(">", Twitter, PowertrackV1).failure
      GnipRuleValidator("?", Twitter, PowertrackV1).failure
      GnipRuleValidator(",", Twitter, PowertrackV1).failure
    }
    "accept single word" in {
      GnipRuleValidator("hello", Twitter, PowertrackV1).success
    }
    "accept upcasing word" in {
      GnipRuleValidator("helLo WOrlD", Twitter, PowertrackV1).success
    }
    "accept two words" in {
      GnipRuleValidator("hello world", Twitter, PowertrackV1).success
    }
    "accept special characters in word" in {
      GnipRuleValidator("hello!%&\'*+-./;<=>?,#@world", Twitter, PowertrackV1).success
    }
    "accept single hashtag" in {
      GnipRuleValidator("#yolo", Twitter, PowertrackV1).success
    }
    "not accept special characters in beginning of word" in {
      GnipRuleValidator("!hello", Twitter, PowertrackV1).failure
    }
    "accept multiple words" in {
      GnipRuleValidator("hello? beautiful world!", Twitter, PowertrackV1).success
    }
    "accept multiple words with negation" in {
      GnipRuleValidator("hello? -beautiful world! -foo -bar bla -lol", Twitter, PowertrackV1).success
    }
    "accept quoted word" in {
      GnipRuleValidator("\"hello\"", Twitter, PowertrackV1).success
    }
    "NOT accept wrong quotes" in {
      GnipRuleValidator("'hello'", Twitter, PowertrackV1).failure
      GnipRuleValidator("''hello''", Twitter, PowertrackV1).failure
    }
    "accept quoted words" in {
      GnipRuleValidator("\"hello world!\"", Twitter, PowertrackV1).success
    }
    "accept parentheses in quoted words" in {
      GnipRuleValidator("\" Mountain Time(US & Canada) \"", Twitter, PowertrackV1).success
    }
    "accept parentheses in quoted words in group" in {
      GnipRuleValidator("(bla OR \" Mountain Time(US & Canada) \")", Twitter, PowertrackV1).success
    }
    "accept quoted negated words" in {
      GnipRuleValidator("\"hello -world!\"", Twitter, PowertrackV1).success
    }
    "accept negated quoted words" in {
      GnipRuleValidator("bla -\"hello world!\"", Twitter, PowertrackV1).success
    }
    "NOT accept negated quoted words in start position" in {
      GnipRuleValidator("-\"hello world!\"", Twitter, PowertrackV1).failure
    }
    "NOT accept only negated quoted words" in {
      GnipRuleValidator("-\"hello world!\" -\"bye world!\"", Twitter, PowertrackV1).failure
    }
    "NOT accept spaces between negation" in {
      GnipRuleValidator("hello - world", Twitter, PowertrackV1).failure
    }
    "accept all combinations of optional negation and quoted words" in {
      GnipRuleValidator("\"hello world?\" bla -bla \"lol!\" bla", Twitter, PowertrackV1).success
    }
    "NOT accept single negated word" in {
      GnipRuleValidator("-hello", Twitter, PowertrackV1).failure
    }
    "NOT accept only negated words" in {
      GnipRuleValidator("-hello -world", Twitter, PowertrackV1).failure
    }
    "NOT accept unfinished quotes" in {
      GnipRuleValidator("\"-hello world\" bla \"lol bla bla", Twitter, PowertrackV1).failure
    }
    "NOT accept empty string" in {
      GnipRuleValidator("", Twitter, PowertrackV1).failure
    }
    "NOT accept single stop word" in {
      GnipRuleValidator("the", Twitter, PowertrackV1).failure
    }
    "NOT accept single stop word 2" in {
      GnipRuleValidator("at", Twitter, PowertrackV1).failure
    }
    "accept stop word combined with non stop word" in {
      GnipRuleValidator("the boat", Twitter, PowertrackV1).success
    }
    "NOT accept only stop words" in {
      GnipRuleValidator("a an and at but by com from http https if in is it its me my or rt the this to too via we www you", Twitter, PowertrackV1).failure
    }
    "NOT accept only stop words 2" in {
      GnipRuleValidator("a an", Twitter, PowertrackV1).failure
    }
    "accept group" in {
      GnipRuleValidator("(the boat)", Twitter, PowertrackV1).success
    }
    "accept groups" in {
      GnipRuleValidator("(the boat) (the other boat)", Twitter, PowertrackV1).success
    }
    "accept nested groups" in {
      GnipRuleValidator("((bla bla))", Twitter, PowertrackV1).success
    }
    "accept nested groups with terms before" in {
      GnipRuleValidator("(the boat (bla bla))", Twitter, PowertrackV1).success
    }
    "accept nested groups with terms after" in {
      GnipRuleValidator("((bla bla) lol lol)", Twitter, PowertrackV1).success
    }
    "accept nested groups with terms before AND after" in {
      GnipRuleValidator("(lol lol (\"bla\" bla) -lol lol)", Twitter, PowertrackV1).success
    }
    "accept groups combined with non-groups" in {
      GnipRuleValidator("the boat (bla bla)", Twitter, PowertrackV1).success
    }
    "accept negated groups" in {
      GnipRuleValidator("the boat -(bla bla)", Twitter, PowertrackV1).success
    }
    "NOT accept negated group in start position" in {
      GnipRuleValidator("-(bla bla)", Twitter, PowertrackV1).failure
    }
    "NOT accept only negated groups" in {
      GnipRuleValidator("-(bla bla) -(lol lol)", Twitter, PowertrackV1).failure
    }
    "accept quoted keywords in groups" in {
      GnipRuleValidator("(\"bla\" \"bla\")", Twitter, PowertrackV1).success
    }
    "NOT accept unclosed groups" in {
      GnipRuleValidator("(hello (world) bla", Twitter, PowertrackV1).failure
    }
    "NOT accept deep unclosed groups" in {
      GnipRuleValidator("(ab ( cd ( ef( gh ( ij ((hello (world) bla) lol) hehe))) xz)", Twitter, PowertrackV1).failure
    }
    "accept single powertrack operator" in {
      GnipRuleValidator("lang:EN", Twitter, PowertrackV1).success
    }
    "NOT accept invalid use of powertrack operator" in {
      GnipRuleValidator("lang:", Twitter, PowertrackV1).failure
    }
    "accept proximity operator" in {
      GnipRuleValidator("\"happy birthday\"~3", Twitter, PowertrackV1).success
    }
    "accept cashtag operator" in {
      GnipRuleValidator("$AAPL lol", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms before" in {
      GnipRuleValidator("bla lang:en", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms after" in {
      GnipRuleValidator("lang:en bla", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla lang:en bla", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms in parentheses before" in {
      GnipRuleValidator("(bla bla) lang:en", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms in parentheses after" in {
      GnipRuleValidator("lang:en (bla bla)", Twitter, PowertrackV1).success
    }
    "accept powertrack operator with terms in parentheses before AND after" in {
      GnipRuleValidator("(bla bla) lang:en (bla bla)", Twitter, PowertrackV1).success
    }
    "accept negated powertrack operator with terms after" in {
      GnipRuleValidator("-lang:en bla", Twitter, PowertrackV1).success
    }
    "accept negated powertrack operator with terms before" in {
      GnipRuleValidator("bla -lang:en", Twitter, PowertrackV1).success
    }
    "NOT accept only negated powertrack operators" in {
      GnipRuleValidator("-lang:en -contains:lol", Twitter, PowertrackV1).failure
    }
    "accept negated powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla -lang:en bla", Twitter, PowertrackV1).success
    }
    "accept multiple powertrack operators" in {
      GnipRuleValidator("lang:en has:links from:8744 contains:help url_contains:foo", Twitter, PowertrackV1).success
    }
    "NOT accept OR missing terms after" in {
      GnipRuleValidator("this OR", Twitter, PowertrackV1).failure
    }
    "NOT accept OR in group missing terms after" in {
      GnipRuleValidator("(gnip OR)", Twitter, PowertrackV1).failure
    }
    "NOT accept multiple OR missing terms after" in {
      GnipRuleValidator("this OR that OR", Twitter, PowertrackV1).failure
    }
    "NOT accept OR missing terms before" in {
      GnipRuleValidator("OR bla", Twitter, PowertrackV1).failure
    }
    "NOT accept negated OR right" in {
      GnipRuleValidator("these OR -that", Twitter, PowertrackV1).failure
    }
    "NOT accept negated OR left" in {
      GnipRuleValidator("-these OR that", Twitter, PowertrackV1).failure
    }
    "accept negated OR followed by positive term(s)" in {
      GnipRuleValidator("these OR -that bla OR these", Twitter, PowertrackV1).success
    }
    "accept OR" in {
      GnipRuleValidator("these OR that", Twitter, PowertrackV1).success
    }
    "NOT accept OR with only stop words left clause" in {
      GnipRuleValidator("this OR that", Twitter, PowertrackV1).failure
    }
    "NOT accept OR with only stop words right clause" in {
      GnipRuleValidator("that OR this", Twitter, PowertrackV1).failure
    }
    "accept ORbit" in {
      GnipRuleValidator("ORbit", Twitter, PowertrackV1).success
    }
    "accept multiple OR" in {
      GnipRuleValidator("these OR that OR these", Twitter, PowertrackV1).success
    }
    "accept OR between groups" in {
      GnipRuleValidator("(these that) OR (that these)", Twitter, PowertrackV1).success
    }
    "accept OR in groups" in {
      GnipRuleValidator("(these OR those)", Twitter, PowertrackV1).success
    }
    "accept OR between group and keyword" in {
      GnipRuleValidator("(these that) OR that", Twitter, PowertrackV1).success
    }
    "accept OR between keyword and group" in {
      GnipRuleValidator("that OR (these that)", Twitter, PowertrackV1).success
    }
    "accept OR between quotes" in {
      GnipRuleValidator("\"the boat\" OR \"that boat\"", Twitter, PowertrackV1).success
    }
    "accept OR between quote and keyword" in {
      GnipRuleValidator("\"the boat\" OR that", Twitter, PowertrackV1).success
    }
    "accept OR between keyword and quote" in {
      GnipRuleValidator("that OR \"the boat\"", Twitter, PowertrackV1).success
    }
    "accept bounding box operator" in {
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505 40.09455]", Twitter, PowertrackV1).success
    }
    "NOT accept wrong use of bounding box operator" in {
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505]", Twitter, PowertrackV1).failure
      GnipRuleValidator("profile_bounding_box:[-105.301758 39.964069 -105.178505 45 4]", Twitter, PowertrackV1).failure
      GnipRuleValidator("profile_bounding_box:[-105.301758 -105.178505]", Twitter, PowertrackV1).failure
    }
    "NOT accept special ops for normal ops operator" in {
      GnipRuleValidator("country_code:[-105.301758 39.964069 -105.178505]", Twitter, PowertrackV1).failure
    }
    "accept profile_point_radius operator" in {
      GnipRuleValidator("profile_point_radius:[-105.27346517 40.01924738 10km]", Twitter, PowertrackV1).success
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 10.2mi]", Twitter, PowertrackV1).success
    }
    "NOT accept wrong use of profile_point_radius operator" in {
      GnipRuleValidator("profile_point_radius:[-105.27346517 40.01924738 10 km]", Twitter, PowertrackV1).failure
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 10.2m]", Twitter, PowertrackV1).failure
      GnipRuleValidator("profile_point_radius:[105.27346517 40.01924738 40.01924738 10.2mi]", Twitter, PowertrackV1).failure
      GnipRuleValidator("profile_point_radius:[105.27346517 10.2mi]", Twitter, PowertrackV1).failure
    }
    "accept statuses_count operator" in {
      GnipRuleValidator("statuses_count:1000..10000", Twitter, PowertrackV1).success
      GnipRuleValidator("statuses_count:1000", Twitter, PowertrackV1).success
    }
    "accept klout_score operator" in {
      GnipRuleValidator("klout_score:1000..10000", Twitter, PowertrackV1).success
      GnipRuleValidator("klout_score:1000", Twitter, PowertrackV1).success
    }
    "accept time_zone operator" in {
      GnipRuleValidator("contains:\" Mountain Time  \"", Twitter, PowertrackV1).success
    }
    "NOT accept wrong use of statuses_count operator" in {
      GnipRuleValidator("statuses_count:1000...10000", Twitter, PowertrackV1).failure
      GnipRuleValidator("statuses_count:1000.0", Twitter, PowertrackV1).failure
      GnipRuleValidator("statuses_count:-900", Twitter, PowertrackV1).failure
    }
    "NOT accept wrong use of lang operator" in {
      GnipRuleValidator("lang:xx", Twitter, PowertrackV1).failure
      GnipRuleValidator("lang:eng", Twitter, PowertrackV1).failure
    }
    "NOT accept wrong use of country_code operator" in {
      GnipRuleValidator("country_code:XX", Twitter, PowertrackV1).failure
      GnipRuleValidator("country_code:USA", Twitter, PowertrackV1).failure
    }
    "NOT accept country_code operator for Gnip 2.0" in {
      GnipRuleValidator("country_code:uk", Twitter, Powertrack2_0).failure
    }
    "NOT accept profile_country operator for Gnip v1" in {
      GnipRuleValidator("profile_country:uk", Twitter, PowertrackV1).failure
    }
    "accept profile_country operator for Gnip 2.0" in {
      GnipRuleValidator("profile_country:uk", Twitter, Powertrack2_0).failure
    }
    "accept quoted keywords as operator param" in {
      GnipRuleValidator("profile_subregion:\"San Francisco County\"", Twitter, PowertrackV1).success
    }
    "accept unicode chars in keyword" in {
      GnipRuleValidator("bla{he˽lʲʳʴʵʶʷʸʹʺʻʼʽʾʿˀˁ˂˃˄˅ˆˇˈˉˊˋˌˍˎˏ₩₪₫€₭₮₯₰ːˑ˒˓˔˕˖˗˘˙˚˛˜˝˞˟ˠˡˢˣˤ˥˦˧˨˩˪˫ˬ˭ˮ˯˰˱˲˳˴˵˶˷˸˹˺˻˼˽˾˿̴̵̶̷̸̡̢̧̨̛̖̗̘̙̜̝̞̟̠ͰͱͲͳʹ͵Ͷͷlo", Twitter, PowertrackV1).success
    }
    "accept French accents on chars in keyword" in {
      GnipRuleValidator("ëèéê", Twitter, PowertrackV1).success
    }
    "accept Chinese characters in keyword" in {
      GnipRuleValidator("你好，世界", Twitter, PowertrackV1).success
    }
    "accept Arabic characters in keyword" in {
      GnipRuleValidator("مرحبا بالعالم", Twitter, PowertrackV1).success
    }
    "accept full syntax" in {
      GnipRuleValidator("(gnip OR from:688583 OR @gnip OR datasift) (\"powertrack -operators\" OR (-\"streaming code\"~4 foo OR bar)) -contains:help has:links url_contains:github bio_contains:developer has:links url_contains:github source:web (friends_count:1 OR followers_count:2000 OR listed_count:500 OR statuses_count:1000..10000 OR is:verified OR klout_score:50) (country_code:US OR (bio_location:CO OR bio_location_contains:\"Boulder\") OR time_zone:\"Mountain Time (US & Canada)\") -is:retweet (lang:en OR twitter_lang:en)", Twitter, PowertrackV1).success
    }
  }

}
