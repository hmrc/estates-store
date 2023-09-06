/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import base.SpecBase
import models.register.Operations._
import models.register.{TaskCache, Tasks}
import org.mockito.ArgumentMatchers.{eq => mEq, _}
import org.mockito.Mockito._
import repositories.EstateRegisterTasksRepository

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EstatesRegisterTasksServiceSpec extends SpecBase {

  private val repository = mock(classOf[EstateRegisterTasksRepository])

  private val service = new RegisterTasksService(repository)

  private val taskTypes = Seq(UpdateDetails, UpdatePersonalRepresentative, UpdateDeceased, UpdateTaxLiability)

  private def defaultTask = Tasks(details = true, personalRepresentative = true, deceased = true, yearsOfTaxLiability = true)

  private def buildExpectedUpdateTask(taskType: UpdateOperation, tasks: Tasks, status: Boolean): Tasks =
    taskType match {
      case UpdateDetails => tasks.copy(details = status)
      case UpdatePersonalRepresentative => tasks.copy(personalRepresentative = status)
      case UpdateDeceased => tasks.copy(deceased = status)
      case UpdateTaxLiability => tasks.copy(yearsOfTaxLiability = status)
    }

  "invoking .get" - {

    "must return a Task from the repository if there is one for the given internal id and utr" in {

      val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

      val taskCache = TaskCache(
        "internalId",
        task,
        LocalDateTime.now
      )

      when(repository.get(mEq("internalId"))).thenReturn(Future.successful(Some(taskCache)))

      val result = service.get("internalId").futureValue

      result mustBe task
    }

    "must return a Task from the repository if there is not one for the given internal id and utr" in {
      val task = Tasks(details = false, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

      when(repository.get(mEq("internalId"))).thenReturn(Future.successful(None))

      val result = service.get("internalId").futureValue

      result mustBe task
    }
  }

  "invoking .set" - {

    "must set default Tasks" in {

      val task = Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)

      when(repository.set(any(), any())).thenReturn(Future.successful(true))

      val result = service.set("internalId", task).futureValue

      result mustBe task
    }

  }

  "invoking .set for an update" - {

    taskTypes.foreach { taskType =>
      s"must set an updated Task of type=$taskType" in {
        val expected = buildExpectedUpdateTask(taskType, defaultTask, status = true)

        when(repository.set(any(), any())).thenReturn(Future.successful(true))

        val result = service.set("internalId", taskType, defaultTask).futureValue

        result mustBe expected
      }
    }

  }

  "invoking .reset for an update" - {

    taskTypes.foreach { taskType =>
      s"must reset an updated Task of type=$taskType" in {
        val expected = buildExpectedUpdateTask(taskType, defaultTask, status = false)

        when(repository.set(any(), any())).thenReturn(Future.successful(true))

        val result = service.reset("internalId", taskType, defaultTask).futureValue

        result mustBe expected
      }
    }

  }
}
