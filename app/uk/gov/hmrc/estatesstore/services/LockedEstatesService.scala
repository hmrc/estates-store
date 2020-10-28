/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.estatesstore.services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateLock
import uk.gov.hmrc.estatesstore.models.claim_an_estate.responses.{GetLockFound, GetLockNotFound, LockedEstateResponse, StoreErrorsResponse, StoreParsingError, StoreSuccessResponse}
import uk.gov.hmrc.estatesstore.repositories.LockedEstatesRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.Session

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class LockedEstatesService @Inject()(private val lockedEstatesRepository: LockedEstatesRepository)  {

  private val logger: Logger = Logger(getClass)

  def get(internalId: String): Future[LockedEstateResponse] = {
    lockedEstatesRepository.get(internalId) map {
      case Some(estateLock) => GetLockFound(estateLock)
      case None => GetLockNotFound
    }
  }

  def store(internalId: String, maybeUtr: Option[String], maybeManagedByAgent: Option[Boolean], maybeEstateLocked: Option[Boolean])(implicit hc: HeaderCarrier): Future[LockedEstateResponse] = {

    val estateLock = (maybeUtr, maybeManagedByAgent, maybeEstateLocked) match {
      case (Some(utr), Some(managedByAgent), None) =>
        logger.info(s"[store][Session ID: ${Session.id(hc)}] Estate is not locked")
        Some(EstateLock(internalId, utr, managedByAgent))
      case (Some(utr), Some(managedByAgent), Some(maybeEstateLocked)) =>
        if (maybeEstateLocked) {logger.info(s"[store][Session ID: ${Session.id(hc)}] Estate is locked")}
        Some(EstateLock(internalId, utr, managedByAgent, maybeEstateLocked))
      case _ => None
    }

    estateLock match {
      case Some(tc) =>
        lockedEstatesRepository.store(tc).map {
          case Left(writeErrors) => StoreErrorsResponse(writeErrors)
          case Right(storedEstateLock) => StoreSuccessResponse(storedEstateLock)
        }
      case None => Future.successful(StoreParsingError)
    }
  }

}