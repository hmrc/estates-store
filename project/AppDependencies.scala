import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val mongoHmrcVersion = "0.73.0"
  private val playBootstrapVersion = "7.7.0"

  val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % mongoHmrcVersion,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % playBootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % playBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % mongoHmrcVersion,
    "com.typesafe.play"       %% "play-test"                  % current,
    "org.scalatest"           %% "scalatest"                  % "3.2.14",
    "org.scalatestplus"       %% "mockito-4-6"                % "3.2.14.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0",
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.62.2"
  ).map(_ % "test, it")

}
