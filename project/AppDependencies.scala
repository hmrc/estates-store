import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "org.reactivemongo"       %% "play2-reactivemongo"        % "0.18.8-play27",
    "uk.gov.hmrc"             %% "bootstrap-backend-play-27"  % "5.3.0"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                  % "3.0.8"                 % "test",
    "com.typesafe.play"       %% "play-test"                  % current                 % "test",
    "org.pegdown"             %  "pegdown"                    % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "4.0.3"                 % "test, it",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "2.27.2"                % "test, it",
    "org.mockito"             %  "mockito-all"                % "1.10.19"               % "test, it"
  )

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )
}
