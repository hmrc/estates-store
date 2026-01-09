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

package services

import base.SpecBase
import models.claim_an_estate.EstateLock
import models.claim_an_estate.responses._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import repositories.LockedEstatesRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LockedEstatesServiceSpec extends SpecBase {

  private val repository = mock(classOf[LockedEstatesRepository])

  private val service = new LockedEstatesService(repository)

  override def beforeEach(): Unit = {
    Mockito.reset(repository)
  }

  "invoking .get" - {
    "must return a GetClaimFoundResponse from the repository if there is one for the given internal id" in {
      val estateLock = EstateLock(internalId = fakeInternalId, utr = fakeUtr, managedByAgent = true)

      when(repository.get(any())).thenReturn(Future.successful(Some(estateLock)))

      val result = service.get("matching-internal-id").futureValue

      result mustBe GetLockFound(estateLock)
    }

    "must return a GetClaimNotFoundResponse from the repository if there is no claims for the given internal id" in {
      when(repository.get(any())).thenReturn(Future.successful(None))

      val result = service.get("unmatched-internal-id").futureValue

      result mustBe GetLockNotFound
    }
  }

  "invoking .store" - {
    "must return a StoreParsingError from the repository if the EstateLock is None" in {
      when(repository.store(any())).thenReturn(Future.successful(None))

      val result = service.store(fakeInternalId, Some(fakeUtr), Some(true), None).futureValue

      result mustBe StoreParsingError
    }

    "must return a StoreSuccessResponse from the repository if the EstateLock is successfully stored" in {

      val estateLock = EstateLock(internalId = fakeInternalId, utr = fakeUtr, managedByAgent = true)

      when(repository.store(any())).thenReturn(Future.successful(Some(estateLock)))

      val result = service.store(fakeInternalId, Some(fakeUtr), Some(true), None).futureValue

      result mustBe StoreSuccessResponse(estateLock)
    }

    "must return a StoreSuccessResponse from the repository if the EstateLock is successfully stored with estateLocked" in {

      val estateLock = EstateLock(internalId = fakeInternalId, utr = fakeUtr, managedByAgent = true)

      when(repository.store(any())).thenReturn(Future.successful(Some(estateLock)))

      val result = service.store(fakeInternalId, Some(fakeUtr), Some(true), Some(true)).futureValue

      result mustBe StoreSuccessResponse(estateLock)
    }

    "must return a StoreParsingErrorResponse if the request body cannot be parsed into a EstateLock" in {
      val result = service.store(fakeInternalId, None, None, None).futureValue

      result mustBe StoreParsingError
    }
  }

}
