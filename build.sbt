import uk.gov.hmrc.DefaultBuildSettings.itSettings

ThisBuild / scalaVersion := "2.13.13"
ThisBuild / majorVersion := 0

val appName = "estates-store"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;.*Routes.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 98,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies              ++= AppDependencies(),
    PlayKeys.playDefaultPort := 8835,
    scalacOptions ++= Seq("-Wconf:src=routes/.*:s"),
    scoverageSettings
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())

addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle it/Test/scalastyle")
