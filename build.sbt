import uk.gov.hmrc.DefaultBuildSettings.itSettings
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

ThisBuild / scalaVersion := "3.6.4"
ThisBuild / targetJvm := "jvm-21"
ThisBuild / majorVersion := 1

lazy val microservice = Project("estates-store", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 8835,
    scalacOptions ++= Seq("-feature", "-Wconf:src=routes/.*:s"),
    CodeCoverageSettings()
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
