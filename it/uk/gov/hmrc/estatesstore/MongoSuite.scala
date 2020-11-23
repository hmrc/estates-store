package uk.gov.hmrc.estatesstore

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.MongoConnection
import repositories.{RegisterTasksRepository, TasksRepository}

import scala.concurrent.ExecutionContext.Implicits.global

trait MongoSuite extends ScalaFutures with IntegrationPatience {

  // Database boilerplate
  private val connectionString = "mongodb://localhost:27017/estates-store-integration"

  def getDatabase(connection: MongoConnection) = {
    connection.database("estates-store-integration")
  }

  def getConnection(application: Application) = {
    val mongoDriver = application.injector.instanceOf[ReactiveMongoApi]

    for {
      uri <- MongoConnection.parseURI(connectionString)
      connection <- mongoDriver.driver.connection(uri, true)
    } yield connection
  }

  def dropTheDatabase(connection: MongoConnection) = {
    getDatabase(connection).flatMap(_.drop())
  }

  def application : Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "mongodb.uri" -> connectionString,
      "metrics.enabled" -> false,
      "auditing.enabled" -> false,
      "mongo-async-driver.akka.log-dead-letters" -> 0
    ): _*)
    .overrides(
      bind[TasksRepository].to(classOf[RegisterTasksRepository])
    ).build()

}
