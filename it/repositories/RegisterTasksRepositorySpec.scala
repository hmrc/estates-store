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

package repositories

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.test.Helpers._
import models.register.{TaskCache, Tasks}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.estatesstore.MongoSuite

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

class RegisterTasksRepositorySpec extends AnyFreeSpec with Matchers
  with ScalaFutures with OptionValues with Inside with MongoSuite with EitherValues {

  val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"

  def assertMongoTest(application: Application)(block: Application => Assertion) : Future[Assertion] =
    running(application) {
      for {
        connection <- getConnection(application)
        _ <- dropTheDatabase(connection)
      } yield block(application)
  }

  "a tasks repository" - {

    "must return None when no cache exists" in assertMongoTest(application) {
      app =>
        val repository = app.injector.instanceOf[TasksRepository]
        repository.get[TaskCache](internalId).futureValue mustBe None
      }

    "must return TaskCache when one exists" in assertMongoTest(application) { app =>
        val repository = app.injector.instanceOf[TasksRepository]

        val task = Tasks(details = false, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

        val cache = TaskCache(internalId, task)

        val initial = repository.set(internalId, cache).futureValue

        initial mustBe true

        repository.get[TaskCache](internalId).futureValue.value.tasks mustBe task
      }

    "must set an updated Task and return one that exists for that user" in assertMongoTest(application) { app =>
        val repository = app.injector.instanceOf[TasksRepository]

        val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

        val initial = TaskCache(internalId, task)

        repository.set(internalId, initial).futureValue

        repository.get[TaskCache](internalId).futureValue.value.tasks mustBe task

        // update

        val updatedTask = Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)

        val updatedCache = TaskCache(internalId, updatedTask)

        repository.set(internalId, updatedCache).futureValue

        repository.get[TaskCache](internalId).futureValue.value.tasks mustBe updatedTask
      }
  }
}
