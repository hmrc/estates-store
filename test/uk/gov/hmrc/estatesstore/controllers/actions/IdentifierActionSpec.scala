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

package uk.gov.hmrc.estatesstore.controllers.actions

import akka.stream.Materializer
import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.estatesstore.SpecBase
import uk.gov.hmrc.estatesstore.connectors.{FakeAuthConnector, FakeFailingAuthConnector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdentifierActionSpec extends FreeSpec with SpecBase with MustMatchers {

  implicit lazy val mtrlzr: Materializer = app.injector.instanceOf[Materializer]

  class Harness(authAction: IdentifierAction) {
    def onSubmit(): Action[JsValue] = authAction.apply(BodyParsers.parse.json) { _ => Results.Ok }
  }

  private def authRetrievals = Future.successful(Some("id"))

  private def insufficientAuthRetrievals =
    Future.successful(None)

  "Auth Action must" - {

    "allow user to continue" in {
      val authAction = new AuthenticatedIdentifierAction(new FakeAuthConnector(authRetrievals), appConfig, bodyParsers)
      val controller = new Harness(authAction)
      val result = controller.onSubmit()(fakeRequest)

      status(result) mustBe OK
    }

    "the user hasn't logged in" - {

      "be returned an unauthorized response" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), appConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onSubmit()(fakeRequest)

        status(result) mustBe UNAUTHORIZED
      }
    }

    "the user's session has expired" - {

      "be returned an unauthorized response" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), appConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onSubmit()(fakeRequest)

        status(result) mustBe UNAUTHORIZED
      }
    }

    "handle insufficient retrievals" - {

      "by returning an unauthorized response" in {
        val authAction = new AuthenticatedIdentifierAction(new FakeAuthConnector(insufficientAuthRetrievals), appConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onSubmit()(fakeRequest)

        status(result) mustBe UNAUTHORIZED
      }
    }
  }
}



