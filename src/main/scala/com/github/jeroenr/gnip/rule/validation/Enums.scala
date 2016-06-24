package com.github.jeroenr.gnip.rule.validation

/**
 * Created by jero on 22-6-16.
 */
sealed trait Source {
  val id: String
}

case object Twitter extends Source { val id: String = "twitter" }
case object Disqus extends Source { val id: String = "disqus" }
case object IntenseDebate extends Source { val id: String = "intensedebate" }
case object Wordpress extends Source { val id: String = "wordpress" }

sealed trait PowertrackVersion {
  val id: String
}

case object PowertrackV1 extends PowertrackVersion { val id: String = "v1" }
case object Powertrack2_0 extends PowertrackVersion { val id: String = "2.0" }

