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

package models

import base.SpecBase
import play.api.http.Status._
import play.api.libs.json.Json
import reactivemongo.api.commands.WriteError
import models.responses.ErrorResponse
import models.responses.ErrorResponse._
import models.claim_an_estate.responses.LockedEstateResponse._
import models.repository.StorageErrors

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

    "must be able to provide a json object with additional storage errors" in {

      val expectedJson =
        Json.parse(
          """
            |{
            |  "status": 500,
            |  "message": "unable to store to estates store",
            |  "errors": [
            |    {
            |      "index 2": [{ "code": 50, "message": "some other mongo write error :(!" }]
            |    },
            |    {
            |      "index 1": [
            |        { "code": 50, "message": "some mongo write error!" },
            |        { "code": 120, "message": "another mongo write error!" }
            |      ]
            |    }
            |  ]
            |}
          """.stripMargin
        )

      val storageErrors = StorageErrors(
        Seq(
          WriteError(index = 1, code = 50, "some mongo write error!"),
          WriteError(index = 1, code = 120, "another mongo write error!"),
          WriteError(index = 2, code = 50, "some other mongo write error :(!")
        )
      ).toJson

      val errorResponseJson = Json.toJson(ErrorResponse(status = INTERNAL_SERVER_ERROR, message = UNABLE_TO_STORE, errors = Some(storageErrors)))

      errorResponseJson mustBe expectedJson
    }
  }
}
