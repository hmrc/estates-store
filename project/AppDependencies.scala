import sbt.*

object AppDependencies {

  private val mongoHmrcVersion = "1.3.0"
  private val playBootstrapVersion = "7.21.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % mongoHmrcVersion,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % playBootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % playBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % mongoHmrcVersion,
    "org.scalatest"           %% "scalatest"                  % "3.2.16",
    "org.mockito"             %  "mockito-core"               % "5.5.0",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.64.8"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test

}
