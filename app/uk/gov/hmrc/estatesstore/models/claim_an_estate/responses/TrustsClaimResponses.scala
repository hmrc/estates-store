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

package uk.gov.hmrc.estatesstore.models.claim_an_estate.responses

import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateClaim
import uk.gov.hmrc.estatesstore.models.repository.StorageErrors

trait ClaimedEstateResponse

object ClaimedEstateResponse {
  val CLAIM_ESTATE_UNABLE_TO_LOCATE = "unable to locate an EstateClaim for the given requests internalId"
  val CLAIM_ESTATE_UNABLE_TO_PARSE = "Unable to parse request body into a EstateClaim"
}

case class GetClaimFound(foundEstateClaim: EstateClaim) extends ClaimedEstateResponse
case object GetClaimNotFound extends ClaimedEstateResponse

case class StoreErrorsResponse(errors: StorageErrors) extends ClaimedEstateResponse
case class StoreSuccessResponse(storedEstateClaim: EstateClaim) extends ClaimedEstateResponse
case object StoreParsingError extends ClaimedEstateResponse