import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"               %% "simple-reactivemongo"       % "8.0.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % "5.10.0"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                  % "3.2.9"                 % "test",
    "com.typesafe.play"       %% "play-test"                  % current                 % "test",
    "org.pegdown"             %  "pegdown"                    % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"                 % "test, it",
    "org.scalatestplus"       %% "scalatestplus-mockito"      % "1.0.0-M2"              % "test",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "2.27.2"                % "test, it",
    "org.mockito"             %  "mockito-all"                % "1.10.19"               % "test, it",
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.35.10"               % "test, it"
  )

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
