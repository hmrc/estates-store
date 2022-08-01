/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.LocalDateTime

import base.SpecBase
import org.mockito.Matchers.{eq => mEq, _}
import org.mockito.Mockito._
import play.api.Application
import play.api.inject.bind
import config.annotations.Register
import models.register.Operations.{UpdatePersonalRepresentative, UpdateTaxLiability}
import models.register.{TaskCache, Tasks}
import repositories.TasksRepository

import scala.concurrent.Future

class RegisterTasksServiceSpec extends SpecBase {

  private val repository = mock[TasksRepository]

  lazy val application: Application = applicationBuilder()
    .overrides(
      bind(classOf[TasksRepository]).qualifiedWith(classOf[Register]).toInstance(repository)
    ).build()

  private val service = application.injector.instanceOf[RegisterTasksService]

  "invoking .get" - {

    "must return a Task from the repository if there is one for the given internal id and utr" in {

      val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

      val taskCache = TaskCache(
        "internalId",
        task,
        LocalDateTime.now
      )

      when(repository.get[TaskCache](mEq("internalId"))(any())).thenReturn(Future.successful(Some(taskCache)))

      val result = service.get("internalId").futureValue

      result mustBe task
    }

    "must return a Task from the repository if there is not one for the given internal id and utr" in {
      val task = Tasks(details = false, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

      when(repository.get[TaskCache](mEq("internalId"))(any())).thenReturn(Future.successful(None))

      val result = service.get("internalId").futureValue

      result mustBe task
    }
  }

  "invoking .set" - {

    "must set default Tasks" in {

      val task = Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)

      when(repository.set[TaskCache](any(), any())(any())).thenReturn(Future.successful(true))

      val result = service.set("internalId", task).futureValue

      result mustBe task
    }

  }

  "invoking .set for an update" - {

    "must set an updated Task" in {

      val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

      when(repository.set[TaskCache](any(), any())(any())).thenReturn(Future.successful(true))

      val result = service.set("internalId", UpdatePersonalRepresentative, task).futureValue

      result mustBe Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)
    }

  }

  "invoking .reset for an update" - {

    "must reset an updated Task" in {

      val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = true)

      when(repository.set[TaskCache](any(), any())(any())).thenReturn(Future.successful(true))

      val result = service.reset("internalId", UpdateTaxLiability, task).futureValue

      result mustBe Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)
    }

  }
}
