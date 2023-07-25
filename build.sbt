import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "estates-store"

lazy val IntegrationTest = config("it") extend Test

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;.*Reverse.*;.*Routes.*;.*filters.*;.*handlers.*;.*components.*;.*Session.*;" +
      "prod.*;testOnlyDoNotUseInProd.*;testOnlyDoNotUseInAppConf.*;uk.gov.hmrc.BuildInfo;app.*;prod.*;config.*;.*AppConfig",
    ScoverageKeys.coverageMinimumStmtTotal := 93,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    scalaVersion := "2.13.11",
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    majorVersion                     := 0,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    PlayKeys.playDefaultPort := 8835,
    RoutesKeys.routesImport := Seq.empty,
    publishingSettings,
    scoverageSettings)
  .settings(inConfig(Test)(testSettings))
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(itSettings): _*)
  .settings(
    inConfig(IntegrationTest)(itSettings),
    integrationTestSettings()
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork                         := true,
  parallelExecution            := false,
  javaOptions                  ++= Seq(
    "-Dconfig.resource=test.application.conf",
    "-Dlogger.resource=logback-test.xml"
  )
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  unmanagedSourceDirectories   := Seq(
    baseDirectory.value / "it"
  ),
  unmanagedResourceDirectories := Seq(
    baseDirectory.value / "it" / "resources"
  ),
  parallelExecution            := false,
  fork                         := true
)

addCommandAlias("scalastyleAll", "all scalastyle test:scalastyle it:scalastyle")
