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

package controllers

import base.SpecBase
import models.register.Operations.UpdateTaxLiability
import models.register.Tasks
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.Application
import play.api.http.Status
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.RegisterTasksService

import scala.concurrent.Future

class RegisterTaskListControllerSpec extends SpecBase {
  private val tasksEndpoint                  = "/estates-store/register/tasks"
  private val estateDetailsEndpoint          = "/estates-store/register/tasks/estate-details"
  private val personalRepresentativeEndpoint = "/estates-store/register/tasks/personal-representative"
  private val deceasedEndpoint               = "/estates-store/register/tasks/deceased"
  private val taxLiabilityEndpoint           = "/estates-store/register/tasks/tax-liability"
  private val resetTaxLiabilityEndpoint      = "/estates-store/register/tasks/tax-liability/reset"

  lazy val application: Application = applicationBuilder()
    .overrides(
      bind[RegisterTasksService].toInstance(service)
    )
    .build()

  private val service: RegisterTasksService = mock(classOf[RegisterTasksService])

  override def beforeEach(): Unit =
    reset(service)

  "invoking GET /register/tasks" - {

    "must return OK and the completed Tasks" in {
      val request = FakeRequest(GET, tasksEndpoint)

      val tasks = Tasks(
        details = true,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))

      val result = route(application, request).value

      status(result)        mustBe Status.OK
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

      val request =
        FakeRequest(POST, tasksEndpoint).withBody(Json.toJson(tasks))

      when(service.set(any(), any())).thenReturn(Future.successful(tasks))

      val result = route(application, request).value

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(tasks)
      verify(service).set("id", tasks)

    }
    "must return BAD_REQUEST for invalid JSON" in {

      val request = FakeRequest(POST, tasksEndpoint).withBody(Json.obj())

      val result = route(application, request).value

      status(result) mustBe Status.BAD_REQUEST
    }

  }

  "invoking POST /register/tasks/tax-liability/reset" - {

    "must return OK and the tasks with Tax Liability reset to false" in {
      val tasks = Tasks(
        details = true,
        personalRepresentative = false,
        deceased = false,
        yearsOfTaxLiability = true
      )

      val updatedTasks = tasks.copy(yearsOfTaxLiability = false)

      val request =
        FakeRequest(POST, resetTaxLiabilityEndpoint).withBody(Json.toJson(tasks))

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.reset(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
      verify(service).reset("id", UpdateTaxLiability, tasks)

    }
    "must return BAD_REQUEST for invalid JSON" in {

      val request = FakeRequest(POST, tasksEndpoint).withBody(Json.obj())

      val result = route(application, request).value

      status(result) mustBe Status.BAD_REQUEST
    }

  }

  "invoking POST /register/tasks/estate-details" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, estateDetailsEndpoint)

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

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/personal-representative" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, personalRepresentativeEndpoint)

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

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/deceased" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, deceasedEndpoint)

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

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/tax-liability" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, taxLiabilityEndpoint)

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

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

  "invoking POST /register/tasks/tax-liability/reset" - {

    "must return Ok and the completed tasks" in {
      val request = FakeRequest(POST, resetTaxLiabilityEndpoint)

      val tasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = true,
        yearsOfTaxLiability = true
      )

      val updatedTasks = Tasks(
        details = false,
        personalRepresentative = false,
        deceased = true,
        yearsOfTaxLiability = false
      )

      when(service.get(any())).thenReturn(Future.successful(tasks))
      when(service.reset(any(), any(), any())).thenReturn(Future.successful(updatedTasks))

      val result = route(application, request).value

      status(result)        mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedTasks)
    }

  }

}
