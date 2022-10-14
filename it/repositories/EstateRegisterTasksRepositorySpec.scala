/*
 * Copyright 2022 HM Revenue & Customs
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

import models.register.{TaskCache, Tasks}
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.ExecutionContext.Implicits.global

class EstateRegisterTasksRepositorySpec extends RepositoriesBase with MongoSupport {

  val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"
  val repository = new EstateRegisterTasksRepository(mongoComponent, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    cleanupDB(repository.collection)
  }

  "a tasks repository" - {

    "must return None when no cache exists" in {
      repository.get(internalId).futureValue mustBe None
    }

    "must return TaskCache when one exists" in {
      val task = Tasks(details = false, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)
      val cache = TaskCache(internalId, task)

      repository.set(internalId, cache).futureValue mustBe true
      repository.get(internalId).futureValue.value.tasks mustBe task
    }

    "must set an updated Task and return one that exists for that user" in {
      val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)
      val initial = TaskCache(internalId, task)

      repository.set(internalId, initial).futureValue mustBe true
      repository.get(internalId).futureValue.value.tasks mustBe task

      // update

      val updatedTask = Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)
      val updatedCache = TaskCache(internalId, updatedTask)

      repository.set(internalId, updatedCache).futureValue mustBe true
      repository.get(internalId).futureValue.value.tasks mustBe updatedTask
    }
  }

}
