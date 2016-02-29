package com.github.jeroenr.gnip.rule.validation

import org.scalatest._

class GnipRuleValidatorSpec extends WordSpec with MustMatchers with TryValues {
  "Gnip rule parser" should {
    "NOT accept missing closed bracket" in {
      GnipRuleValidator("(ab ( cd ( ef( lol) hehe) xz)").failure
      GnipRuleValidator("(ab ( cd ( ef( gh ( ij ((hello (world) bla) lol) hehe))) xz)").failure
    }
  }

}
