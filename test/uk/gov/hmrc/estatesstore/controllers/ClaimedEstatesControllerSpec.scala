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

package uk.gov.hmrc.estatesstore.controllers

import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito._
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import reactivemongo.api.commands.WriteError
import uk.gov.hmrc.estatesstore.SpecBase
import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateClaim
import uk.gov.hmrc.estatesstore.models.claim_an_estate.responses.{GetClaimFound, GetClaimNotFound, StoreErrorsResponse, StoreParsingError, StoreSuccessResponse}
import uk.gov.hmrc.estatesstore.models.repository.StorageErrors
import uk.gov.hmrc.estatesstore.services.ClaimedEstatesService

import scala.concurrent.Future


class ClaimedEstatesControllerSpec extends SpecBase {


  private val service: ClaimedEstatesService = mock[ClaimedEstatesService]

  lazy val application: Application = applicationBuilder().overrides(
    bind[ClaimedEstatesService].toInstance(service)
  ).build()

  override def beforeEach(): Unit = {
    Mockito.reset(service)
  }

  "invoking GET /claim" - {
    "must return OK and a EstateClaim if there is one for the internal id" in {
      val request = FakeRequest(GET, routes.ClaimedEstatesController.get().url)

      val estateClaim = EstateClaim(internalId = fakeInternalId, utr = fakeUtr, managedByAgent = true)

      when(service.get(any())).thenReturn(Future.successful(GetClaimFound(estateClaim)))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe estateClaim.toResponse
    }

    "must return NOT_FOUND if there is no EstateClaim for the internal id" in {
      val request = FakeRequest(GET, routes.ClaimedEstatesController.get().url)

      val expectedJson = Json.parse(
        """
          |{
          |  "status": 404,
          |  "message": "unable to locate an EstateClaim for the given requests internalId"
          |}
        """.stripMargin
      )

      when(service.get(any())).thenReturn(Future.successful(GetClaimNotFound))

      val result = route(application, request).value

      status(result) mustBe Status.NOT_FOUND
      contentAsJson(result) mustBe expectedJson
    }
  }

  "invoking POST /claim" - {
    "must return CREATED and the stored EstateClaim if the service returns a StoreSuccessResponse" in {
      val request = FakeRequest(POST, routes.ClaimedEstatesController.store().url)
        .withJsonBody(Json.obj(
          "utr" -> "0123456789",
          "managedByAgent" -> true
        ))

      val estateClaim = EstateClaim(internalId = fakeInternalId, utr = fakeUtr, managedByAgent = true)

      when(service.store(any(), any(), any(), any())).thenReturn(Future.successful(StoreSuccessResponse(estateClaim)))

      val result = route(application, request).value

      status(result) mustBe Status.CREATED
      contentAsJson(result) mustBe estateClaim.toResponse
    }

    "must return BAD_REQUEST and an error response if the service returns a StoreParsingErrorResponse" in {
      val request = FakeRequest(POST, routes.ClaimedEstatesController.store().url)
        .withJsonBody(Json.obj(
          "some-incorrect-key" -> "some-incorrect-value"
        ))

      val expectedJson = Json.parse(
        """
          |{
          |  "status": 400,
          |  "message": "Unable to parse request body into a EstateClaim"
          |}
        """.stripMargin
      )

      when(service.store(any(), any(), any(), any())).thenReturn(Future.successful(StoreParsingError))

      val result = route(application, request).value

      status(result) mustBe Status.BAD_REQUEST
      contentAsJson(result) mustBe expectedJson
    }

    "must return INTERNAL_SERVER_ERROR and an error response if the service returns a StoreErrorsResponse" in {
      val request = FakeRequest(POST, routes.ClaimedEstatesController.store().url)
        .withJsonBody(Json.obj(
          "some-incorrect-key" -> "some-incorrect-value"
        ))

      val storageErrors = StorageErrors(
        Seq(
          WriteError(index = 0, code = 100, "some mongo write error!"),
          WriteError(index = 1, code = 100, "another mongo write error!"),
          WriteError(index = 0, code = 200, "a different mongo write error!")
        )
      )

      val expectedJson = Json.parse(
        """
          |{
          |  "status": 500,
          |  "message": "unable to store to estates store",
          |  "errors": [
          |    { "index 1": [{ "code": 100, "message": "another mongo write error!" }] },
          |    {
          |      "index 0": [
          |        { "code": 100, "message": "some mongo write error!" },
          |        { "code": 200, "message": "a different mongo write error!" }
          |      ]
          |    }
          |  ]
          |}
        """.stripMargin
      )
      when(service.store(any(), any(), any(), any())).thenReturn(Future.successful(StoreErrorsResponse(storageErrors)))

      val result = route(application, request).value

      status(result) mustBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) mustBe expectedJson
    }
  }

}
