import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private val mongoHmrcVersion = "1.3.0"
  private val playBootstrapVersion = "7.20.0"

  val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % mongoHmrcVersion,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % playBootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % playBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % mongoHmrcVersion,
    "com.typesafe.play"       %% "play-test"                  % "2.8.20",
    "org.scalatest"           %% "scalatest"                  % "3.2.16",
    "org.scalatestplus"       %% "mockito-4-6"                % "3.2.15.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0",
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.64.8"
  ).map(_ % "test, it")

}
