import uk.gov.hmrc.DefaultBuildSettings.itSettings

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / majorVersion := 0

val appName = "estates-store"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies              ++= AppDependencies(),
    PlayKeys.playDefaultPort := 8835,
    scalacOptions ++= Seq("-Wconf:src=routes/.*:s"),
    CodeCoverageSettings()
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())
