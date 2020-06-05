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

package uk.gov.hmrc.estatesstore.services

import javax.inject.Inject
import uk.gov.hmrc.estatesstore.config.annotations.Register
import uk.gov.hmrc.estatesstore.models.register.Operations.{UpdateDeceased, UpdateDetails, UpdateOperation, UpdatePersonalRepresentative, UpdateTaxLiability}
import uk.gov.hmrc.estatesstore.models.register.{TaskCache, Tasks}
import uk.gov.hmrc.estatesstore.repositories.TasksRepository

import scala.concurrent.{ExecutionContext, Future}

class RegisterTasksService @Inject()(@Register tasksRepository: TasksRepository)(implicit ec: ExecutionContext) {

  def get(internalId: String): Future[Tasks] = {
    tasksRepository.get[TaskCache](internalId) map {
      case Some(cache) => cache.tasks
      case None =>
        Tasks(
          details = false,
          personalRepresentative = false,
          deceased = false,
          yearsOfTaxLiability = false
        )
    }
  }

  def set(internalId: String, tasks: Tasks) : Future[Tasks] = {
    val cache = TaskCache(internalId, tasks)
    save(internalId, cache)
  }

  def set(internalId: String, operation: UpdateOperation, tasks: Tasks) : Future[Tasks] = {
    updateTask(operation, tasks) flatMap {
      updated =>
        val cache = TaskCache(internalId, updated)
        save(internalId, cache)
    }
  }

  private def save(internalId: String, cache: TaskCache) = {
    tasksRepository.set[TaskCache](internalId, cache).map(_ => cache.tasks)
  }

  private def updateTask(operation: UpdateOperation, tasks: Tasks) = {
    Future.successful {
      operation match {
        case UpdateDetails => tasks.copy(details = true)
        case UpdatePersonalRepresentative => tasks.copy(personalRepresentative = true)
        case UpdateDeceased => tasks.copy(deceased = true)
        case UpdateTaxLiability => tasks.copy(yearsOfTaxLiability = true)
      }
    }
  }

}