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

package models

import base.SpecBase
import models.claim_an_estate.responses.LockedEstateResponse._
import models.responses.ErrorResponse
import models.responses.ErrorResponse._
import play.api.http.Status._
import play.api.libs.json.Json

class ErrorResponseSpec extends SpecBase {


  "ErrorResponse" - {

    "must be able to provide a minimal json object with an error" in {

      val expectedJson =
        Json.parse(
          """
            |{
            | "status": 404,
            | "message": "unable to locate an EstateLock for the given requests internalId"
            |}
          """.stripMargin
        )

      val errorResponseJson = Json.toJson(ErrorResponse(status = NOT_FOUND, message = LOCKED_ESTATE_UNABLE_TO_LOCATE, errors = None))

      errorResponseJson mustBe expectedJson
    }
  }
}
