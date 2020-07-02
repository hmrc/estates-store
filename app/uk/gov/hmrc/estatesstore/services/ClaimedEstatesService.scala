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
import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateClaim
import uk.gov.hmrc.estatesstore.models.claim_an_estate.responses.{ClaimedEstateResponse, GetClaimFound, GetClaimNotFound, StoreErrorsResponse, StoreParsingError, StoreSuccessResponse}
import uk.gov.hmrc.estatesstore.repositories.ClaimedEstatesRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class ClaimedEstatesService @Inject()(private val claimedEstatesRepository: ClaimedEstatesRepository)  {

  def get(internalId: String): Future[ClaimedEstateResponse] = {
    claimedEstatesRepository.get(internalId) map {
      case Some(estateClaim) => GetClaimFound(estateClaim)
      case None => GetClaimNotFound
    }
  }

  def store(internalId: String, maybeUtr: Option[String], maybeManagedByAgent: Option[Boolean], maybeEstateLocked: Option[Boolean]): Future[ClaimedEstateResponse] = {

    val estateClaim = (maybeUtr, maybeManagedByAgent, maybeEstateLocked) match {
      case (Some(utr), Some(managedByAgent), None) => Some(EstateClaim(internalId, utr, managedByAgent))
      case (Some(utr), Some(managedByAgent), Some(maybeEstateLocked)) => Some(EstateClaim(internalId, utr, managedByAgent, maybeEstateLocked))
      case _ => None
    }

    estateClaim match {
      case Some(tc) =>
        claimedEstatesRepository.store(tc).map {
          case Left(writeErrors) => StoreErrorsResponse(writeErrors)
          case Right(storedEstateClaim) => StoreSuccessResponse(storedEstateClaim)
        }
      case None => Future.successful(StoreParsingError)
    }
  }

}