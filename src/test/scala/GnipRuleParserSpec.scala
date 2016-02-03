
import org.scalatest._

class GnipRuleParserSpec extends WordSpec with MustMatchers with TryValues {

  "Gnip rule parser" should {
    "accept single character" in {
      GnipRuleParser("h").success
    }
    "accept single word" in {
      GnipRuleParser("hello").success
    }
    "accept upcasing word" in {
      GnipRuleParser("helLo WOrlD").success
    }
    "accept two words" in {
      GnipRuleParser("hello world").success
    }
    "accept special characters in word" in {
      GnipRuleParser("hello!%&\'*+-./;<=>?,#@world").success
    }
    "accept single hashtag" in {
      GnipRuleParser("#yolo").success
    }
    "not accept special characters in beginning of word" in {
      GnipRuleParser("!hello").failure
    }
    "accept multiple words" in {
      GnipRuleParser("hello? beautiful world!").success
    }
    "accept multiple words with negation" in {
      GnipRuleParser("hello? -beautiful world! -foo -bar bla -lol").success
    }
    "accept quoted word" in {
      GnipRuleParser("\"hello\"").success
    }
    "accept quoted words" in {
      GnipRuleParser("\"hello world!\"").success
    }
    "accept quoted negated words" in {
      GnipRuleParser("\"hello -world!\"").success
    }
    "accept negated quoted words" in {
      GnipRuleParser("bla -\"hello world!\"").success
    }
    "NOT accept negated quoted words in start position" in {
      GnipRuleParser("-\"hello world!\"").failure
    }
    "NOT accept only negated quoted words" in {
      GnipRuleParser("-\"hello world!\" -\"bye world!\"").failure
    }
    "accept all combinations of optional negation and quoted words" in {
      GnipRuleParser("\"hello world?\" bla -bla \"lol!\" bla").success
    }
    "NOT accept single negated word" in {
      GnipRuleParser("-hello").failure
    }
    "NOT accept only negated words" in {
      GnipRuleParser("-hello -world").failure
    }
    "NOT accept unfinished quotes" in {
      GnipRuleParser("\"-hello world\" bla \"lol bla bla").failure
    }
    "NOT accept empty string" in {
      GnipRuleParser("").failure
    }
    "NOT accept single stop word" in {
      GnipRuleParser("the").failure
    }
    "NOT accept single stop word 2" in {
      GnipRuleParser("at").failure
    }
    "accept stop word combined with non stop word" in {
      GnipRuleParser("the boat").success
    }
    "NOT accept only stop words" in {
      GnipRuleParser("a an and at but by com from http https if in is it its me my or rt the this to too via we www you").failure
    }
    "accept group" in {
      GnipRuleParser("(the boat)").success
    }
    "accept groups" in {
      GnipRuleParser("(the boat) (the other boat)").success
    }
    "accept nested groups" in {
      GnipRuleParser("((bla bla))").success
    }
    "accept nested groups with terms before" in {
      GnipRuleParser("(the boat (bla bla))").success
    }
    "accept nested groups with terms after" in {
      GnipRuleParser("((bla bla) lol lol)").success
    }
    "accept nested groups with terms before AND after" in {
      GnipRuleParser("(lol lol (\"bla\" bla) -lol lol)").success
    }
    "accept groups combined with non-groups" in {
      GnipRuleParser("the boat (bla bla)").success
    }
    "accept negated groups" in {
      GnipRuleParser("the boat -(bla bla)").success
    }
    "NOT accept negated group in start position" in {
      GnipRuleParser("-(bla bla)").failure
    }
    "NOT accept only negated groups" in {
      GnipRuleParser("-(bla bla) -(lol lol)").failure
    }
    "accept quoted keywords in groups" in {
      GnipRuleParser("(\"bla\" \"bla\")").success
    }
    "NOT accept unclosed groups" in {
      GnipRuleParser("(hello (world) bla").failed
    }
    "accept single powertrack operator" in {
      GnipRuleParser("lang:EN").success
    }
    "NOT accept invalid use of powertrack operator" in {
      GnipRuleParser("lang:").failed
    }
    "accept proximity operator" in {
      GnipRuleParser("\"happy birthday\"~3").success
    }
    "accept powertrack operator with terms before" in {
      GnipRuleParser("bla lang:en").success
    }
    "accept powertrack operator with terms in parentheses before" in {
      GnipRuleParser("(bla bla) lang:en").success
    }
    "accept powertrack operator with terms in parentheses after" in {
      GnipRuleParser("lang:en (bla bla)").success
    }
    "accept powertrack operator with terms in parentheses before AND after" in {
      GnipRuleParser("(bla bla) lang:en (bla bla)").success
    }
    "accept negated powertrack operator with terms after" in {
      GnipRuleParser("-lang:en bla").success
    }
    "accept negated powertrack operator with terms before" in {
      GnipRuleParser("bla -lang:en").success
    }
    "NOT accept only negated powertrack operators" in {
      GnipRuleParser("-lang:en -contains:lol").failure
    }
    "accept negated powertrack operator with terms before AND after" in {
      GnipRuleParser("bla -lang:en bla").success
    }
    "accept multiple powertrack operators" in {
      GnipRuleParser("lang:en has:links from:8744 contains:help url_contains:foo").success
    }
    "accept combination of all" in {
      GnipRuleParser("(gnip OR from:688583 OR @gnip) (\"powertrack -operators\" OR \"streaming code\"~4) contains:help -lang:en").success
    }
    "NOT accept OR missing terms after" in {
      GnipRuleParser("this OR").failed // why does this pass?
    }
    "NOT accept OR missing terms before" in {
      GnipRuleParser("OR bla").failed // why does this pass?
    }
    "NOT accept negated OR" in {
      GnipRuleParser("this OR -that").failed // why does this pass?
    }
    "accept OR" in {
      GnipRuleParser("this OR that").success
    }

    //    "accept full syntax" in {
    //      GnipRuleParser("(gnip OR from:688583 OR @gnip) (\"powertrack operators\" OR \"streaming code\"~4) contains:help bio_contains:developer has:links url_contains:github source:web (friends_count:1 OR followers_count:2000 OR listed_count:500 OR statuses_count:1000 OR is:verified OR klout_score:50) (country_code:US OR bio_location:CO OR bio_location_contains:Boulder OR time_zone:\"Mountain Time (US & Canada)\") -is_retweet (lang:en OR twitter_lang:en)").success
    //    }
  }

}
