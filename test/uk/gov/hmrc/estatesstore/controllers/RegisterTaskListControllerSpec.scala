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
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.estatesstore.SpecBase
import uk.gov.hmrc.estatesstore.models.register.Tasks
import uk.gov.hmrc.estatesstore.services.RegisterTasksService

import scala.concurrent.Future

class RegisterTaskListControllerSpec extends FreeSpec with SpecBase with GuiceOneAppPerSuite with MustMatchers
  with MockitoSugar with ScalaFutures with OptionValues {

  private val service: RegisterTasksService = mock[RegisterTasksService]

  lazy val application: Application = applicationBuilder().overrides(
    bind[RegisterTasksService].toInstance(service)
  ).build()

  "invoking GET /register/tasks" - {

    "must return OK and the completed Tasks" in {
      val request = FakeRequest(GET, routes.RegisterTaskListController.get().url)

      val tasks = Tasks(
        details = true,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(tasks)
    }

  }

  "invoking POST /register/tasks" - {

    "must return OK and the completed Tasks for valid tasks" in {
      val tasks = Tasks(
        details = true,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      val request = FakeRequest(POST, routes.RegisterTaskListController.set().url).withBody(Json.toJson(tasks))

      when(service.set(any(), any())).thenReturn(Future.successful(tasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(tasks)
      verify(service).set("id", tasks)

    }
    "must return BAD_REQUEST for invalid JSON" in {

      val request = FakeRequest(POST, routes.RegisterTaskListController.set().url).withBody(Json.obj())

      val result = route(application, request).value

      status(result) mustBe Status.BAD_REQUEST
    }

  }

  "invoking POST /register/tasks/estate-details" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, routes.RegisterTaskListController.completeDetails().url)

      val tasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      val updatedTasks = Tasks(
        details = true,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.set(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/personal-representative" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, routes.RegisterTaskListController.completePersonalRepresentative().url)

      val tasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      val updatedTasks = Tasks(
        details = false,
        personalRepresentative = true,
        deceased = false,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.set(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/deceased" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, routes.RegisterTaskListController.completeDeceased().url)

      val tasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      val updatedTasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = true,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.set(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/tax-liability" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, routes.RegisterTaskListController.completeTaxLiability().url)

      val tasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      val updatedTasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = true
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.set(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

}
