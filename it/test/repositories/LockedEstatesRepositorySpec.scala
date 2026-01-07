/*
 * Copyright 2026 HM Revenue & Customs
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

import models.claim_an_estate.EstateLock
import uk.gov.hmrc.mongo.test.MongoSupport

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global

class LockedEstatesRepositorySpec extends RepositoriesBase with MongoSupport {

  val repository = new LockedEstatesRepository(mongoComponent, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    cleanupDB(repository.collection)
  }

  "a claimed estates repository" - {

    val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"

    "must be able to store, retrieve and remove estates claims" in {
      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateLock = EstateLock(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)

      repository.store(estateLock).futureValue.value mustBe estateLock

      repository.get(internalId).futureValue.value mustBe estateLock

      remove(repository.collection, internalId)

      repository.get(internalId).futureValue mustBe None
    }

    "must be able to update a estate claim with the same auth id" in {
      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateLock = EstateLock(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)
      val updatedEstateLock = estateLock.copy(utr = "0987654321")

      repository.store(estateLock).futureValue.value mustBe estateLock
      repository.store(updatedEstateLock).futureValue.value mustBe updatedEstateLock
    }
  }
}
