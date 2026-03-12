import sbt.*

object AppDependencies {

  private val mongoHmrcVersion     = "2.12.0"
  private val playBootstrapVersion = "10.7.0"

  private val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % playBootstrapVersion
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % playBootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoHmrcVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
