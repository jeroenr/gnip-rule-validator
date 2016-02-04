
import org.scalatest._

class GnipRuleValidatorSpec extends WordSpec with MustMatchers with TryValues {

  "Gnip rule parser" should {
    "accept single character" in {
      GnipRuleValidator("h").success
    }
    "NOT accept single #" in {
      GnipRuleValidator("#").failure
    }
    "NOT accept single @" in {
      GnipRuleValidator("@").failure
    }
    "NOT accept single @ in phrase" in {
      GnipRuleValidator("hello @ world").failure
    }
    "NOT accept single + in phrase" in {
      GnipRuleValidator("hello + world").failure
    }
    "NOT accept single special character" in {
      GnipRuleValidator("+").failure
      GnipRuleValidator("!").failure
      GnipRuleValidator("%").failure
      GnipRuleValidator("&").failure
      GnipRuleValidator("\\").failure
      GnipRuleValidator("'").failure
      GnipRuleValidator("*").failure
      GnipRuleValidator("-").failure
      GnipRuleValidator(".").failure
      GnipRuleValidator("/").failure
      GnipRuleValidator(";").failure
      GnipRuleValidator("<").failure
      GnipRuleValidator("=").failure
      GnipRuleValidator(">").failure
      GnipRuleValidator("?").failure
      GnipRuleValidator(",").failure
    }
    "accept single word" in {
      GnipRuleValidator("hello").success
    }
    "accept upcasing word" in {
      GnipRuleValidator("helLo WOrlD").success
    }
    "accept two words" in {
      GnipRuleValidator("hello world").success
    }
    "accept special characters in word" in {
      GnipRuleValidator("hello!%&\'*+-./;<=>?,#@world").success
    }
    "accept single hashtag" in {
      GnipRuleValidator("#yolo").success
    }
    "not accept special characters in beginning of word" in {
      GnipRuleValidator("!hello").failure
    }
    "accept multiple words" in {
      GnipRuleValidator("hello? beautiful world!").success
    }
    "accept multiple words with negation" in {
      GnipRuleValidator("hello? -beautiful world! -foo -bar bla -lol").success
    }
    "accept quoted word" in {
      GnipRuleValidator("\"hello\"").success
    }
    "accept quoted words" in {
      GnipRuleValidator("\"hello world!\"").success
    }
    "accept quoted negated words" in {
      GnipRuleValidator("\"hello -world!\"").success
    }
    "accept negated quoted words" in {
      GnipRuleValidator("bla -\"hello world!\"").success
    }
    "NOT accept negated quoted words in start position" in {
      GnipRuleValidator("-\"hello world!\"").failure
    }
    "NOT accept only negated quoted words" in {
      GnipRuleValidator("-\"hello world!\" -\"bye world!\"").failure
    }
    "accept all combinations of optional negation and quoted words" in {
      GnipRuleValidator("\"hello world?\" bla -bla \"lol!\" bla").success
    }
    "NOT accept single negated word" in {
      GnipRuleValidator("-hello").failure
    }
    "NOT accept only negated words" in {
      GnipRuleValidator("-hello -world").failure
    }
    "NOT accept unfinished quotes" in {
      GnipRuleValidator("\"-hello world\" bla \"lol bla bla").failure
    }
    "NOT accept empty string" in {
      GnipRuleValidator("").failure
    }
    "NOT accept single stop word" in {
      GnipRuleValidator("the").failure
    }
    "NOT accept single stop word 2" in {
      GnipRuleValidator("at").failure
    }
    "accept stop word combined with non stop word" in {
      GnipRuleValidator("the boat").success
    }
    "NOT accept only stop words" in {
      GnipRuleValidator("a an and at but by com from http https if in is it its me my or rt the this to too via we www you").failure
    }
    "NOT accept only stop words 2" in {
      GnipRuleValidator("a an").failure
    }
    "accept group" in {
      GnipRuleValidator("(the boat)").success
    }
    "accept groups" in {
      GnipRuleValidator("(the boat) (the other boat)").success
    }
    "accept nested groups" in {
      GnipRuleValidator("((bla bla))").success
    }
    "accept nested groups with terms before" in {
      GnipRuleValidator("(the boat (bla bla))").success
    }
    "accept nested groups with terms after" in {
      GnipRuleValidator("((bla bla) lol lol)").success
    }
    "accept nested groups with terms before AND after" in {
      GnipRuleValidator("(lol lol (\"bla\" bla) -lol lol)").success
    }
    "accept groups combined with non-groups" in {
      GnipRuleValidator("the boat (bla bla)").success
    }
    "accept negated groups" in {
      GnipRuleValidator("the boat -(bla bla)").success
    }
    "NOT accept negated group in start position" in {
      GnipRuleValidator("-(bla bla)").failure
    }
    "NOT accept only negated groups" in {
      GnipRuleValidator("-(bla bla) -(lol lol)").failure
    }
    "accept quoted keywords in groups" in {
      GnipRuleValidator("(\"bla\" \"bla\")").success
    }
    "NOT accept unclosed groups" in {
      GnipRuleValidator("(hello (world) bla").failed
    }
    "accept single powertrack operator" in {
      GnipRuleValidator("lang:EN").success
    }
    "NOT accept invalid use of powertrack operator" in {
      GnipRuleValidator("lang:").failed
    }
    "accept proximity operator" in {
      GnipRuleValidator("\"happy birthday\"~3").success
    }
    "accept powertrack operator with terms before" in {
      GnipRuleValidator("bla lang:en").success
    }
    "accept powertrack operator with terms after" in {
      GnipRuleValidator("lang:en bla").success
    }
    "accept powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla lang:en bla").success
    }
    "accept powertrack operator with terms in parentheses before" in {
      GnipRuleValidator("(bla bla) lang:en").success
    }
    "accept powertrack operator with terms in parentheses after" in {
      GnipRuleValidator("lang:en (bla bla)").success
    }
    "accept powertrack operator with terms in parentheses before AND after" in {
      GnipRuleValidator("(bla bla) lang:en (bla bla)").success
    }
    "accept negated powertrack operator with terms after" in {
      GnipRuleValidator("-lang:en bla").success
    }
    "accept negated powertrack operator with terms before" in {
      GnipRuleValidator("bla -lang:en").success
    }
    "NOT accept only negated powertrack operators" in {
      GnipRuleValidator("-lang:en -contains:lol").failure
    }
    "accept negated powertrack operator with terms before AND after" in {
      GnipRuleValidator("bla -lang:en bla").success
    }
    "accept multiple powertrack operators" in {
      GnipRuleValidator("lang:en has:links from:8744 contains:help url_contains:foo").success
    }
    "accept combination of all" in {
      GnipRuleValidator("(gnip OR from:688583 OR @gnip) (\"powertrack -operators\" OR \"streaming code\"~4) contains:help -lang:en").success
    }
    "NOT accept OR missing terms after" in {
      GnipRuleValidator("this OR").failed // why does this pass?
    }
    "NOT accept multiple OR missing terms after" in {
      GnipRuleValidator("this OR that OR").failed // why does this pass?
    }
    "NOT accept OR missing terms before" in {
      GnipRuleValidator("OR bla").failed // why does this pass?
    }
    "NOT accept negated OR" in {
      GnipRuleValidator("this OR -that").failed // why does this pass?
    }
    "accept OR" in {
      GnipRuleValidator("this OR that").success
    }
    "accept full syntax" in {
      GnipRuleValidator("(gnip OR from:688583 OR @gnip OR -datasift) (\"powertrack -operators\" OR (-\"streaming code\"~4 foo OR bar)) -contains:help has:links url_contains:github").success
    }
  }

}
