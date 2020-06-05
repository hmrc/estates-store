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

package uk.gov.hmrc.estatesstore

import org.scalatest.TestSuite
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.MimeTypes
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{BodyParsers, PlayBodyParsers}
import play.api.test.FakeRequest
import play.api.test.Helpers.CONTENT_TYPE
import uk.gov.hmrc.estatesstore.config.AppConfig
import uk.gov.hmrc.estatesstore.controllers.actions.{FakeIdentifierAction, IdentifierAction}

trait SpecBase extends GuiceOneAppPerSuite {
  this: TestSuite =>

  def fakeRequest: FakeRequest[JsValue] = FakeRequest("POST", "")
    .withHeaders(CONTENT_TYPE -> MimeTypes.JSON)
    .withBody(Json.parse("{}"))

  def injectedParsers: PlayBodyParsers = app.injector.instanceOf[PlayBodyParsers]

  def appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  def bodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  protected def applicationBuilder(): GuiceApplicationBuilder = {
    new GuiceApplicationBuilder()
      .configure(
        Seq(
          "metrics.enabled" -> false,
          "auditing.enabled" -> false
        ): _*
      )
      .overrides(
        bind[IdentifierAction].toInstance(new FakeIdentifierAction(injectedParsers))
      )
  }

}
