/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.estatesstore

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.{AsyncDriver, MongoConnection}
import repositories.{EstatesMongoDriver, MongoDriver, RegisterTasksRepository, TasksRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MongoSuite extends ScalaFutures with IntegrationPatience {

  // Database boilerplate
  private val connectionString = "mongodb://localhost:27017/estates-store-integration"

  def getDatabase(connection: MongoConnection) = {
    connection.database("estates-store-integration")
  }

  def getConnection(application: Application) = {
    val mongoDriver = application.injector.instanceOf[ReactiveMongoComponent]

    for {
      uri <- Future.fromTry(MongoConnection.parseURI(connectionString))
      connection <- AsyncDriver().connect(uri)
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
      bind[TasksRepository].to(classOf[RegisterTasksRepository]),
      bind[MongoDriver].to(classOf[EstatesMongoDriver])
    ).build()

}
