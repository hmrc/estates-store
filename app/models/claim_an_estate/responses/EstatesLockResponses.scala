/*
 * Copyright 2024 HM Revenue & Customs
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

package models.claim_an_estate.responses

import models.claim_an_estate.EstateLock

trait LockedEstateResponse

object LockedEstateResponse {
  val LOCKED_ESTATE_UNABLE_TO_LOCATE = "unable to locate an EstateLock for the given requests internalId"
  val LOCKED_ESTATE_UNABLE_TO_PARSE = "Unable to parse request body into a EstateLock"
}

case class GetLockFound(foundEstateLock: EstateLock) extends LockedEstateResponse
case object GetLockNotFound extends LockedEstateResponse

case class StoreSuccessResponse(storedEstateLock: EstateLock) extends LockedEstateResponse
case object StoreParsingError extends LockedEstateResponse
