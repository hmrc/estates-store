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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.test.Helpers._
import models.claim_an_estate.EstateLock
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.estatesstore.MongoSuite

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

class LockedEstatesRepositorySpec extends AnyFreeSpec with Matchers
  with ScalaFutures with OptionValues with Inside with MongoSuite with EitherValues {

  "a claimed estates repository" - {

    val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"

    def assertMongoTest(application: Application)(block: Application => Assertion): Future[Assertion] =
      running(application) {
        for {
          connection <- getConnection(application)
          _ <- dropTheDatabase(connection)
        } yield block(application)
      }

    "must be able to store, retrieve and remove estates claims" in assertMongoTest(application) { app =>

      val repository = app.injector.instanceOf[LockedEstatesRepository]

      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateLock = EstateLock(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)

      val storedClaim = repository.store(estateLock).futureValue.right.value

      inside(storedClaim) {
        case EstateLock(id, utr, mba, tl, ldt) =>
          id mustEqual internalId
          utr mustEqual storedClaim.utr
          mba mustEqual storedClaim.managedByAgent
          tl mustEqual storedClaim.estateLocked
          ldt mustEqual storedClaim.lastUpdated
      }

      repository.get(internalId).futureValue.value mustBe estateLock

      repository.remove(internalId).futureValue

      repository.get(internalId).futureValue mustNot be(defined)
    }

    "must be able to update a estate claim with the same auth id" in assertMongoTest(application) { app =>

      val repository = app.injector.instanceOf[LockedEstatesRepository]

      val lastUpdated = LocalDateTime.parse("2000-01-01 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

      val estateLock = EstateLock(internalId, "1234567890", managedByAgent = true, lastUpdated = lastUpdated)

      repository.store(estateLock).futureValue.right.value

      val updatedClaim = repository.store(estateLock.copy(utr = "0987654321")).futureValue

      updatedClaim must be('right)
    }
  }
}
