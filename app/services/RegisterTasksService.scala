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

import models.register.Operations._
import models.register.{TaskCache, Tasks}
import repositories.EstateRegisterTasksRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegisterTasksService @Inject()(tasksRepository: EstateRegisterTasksRepository)(implicit ec: ExecutionContext) {

  def get(internalId: String): Future[Tasks] = {
    tasksRepository.get(internalId) map {
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
    tasksRepository.set(internalId, cache).map(_ => cache.tasks)
  }

  def set(internalId: String, operation: UpdateOperation, tasks: Tasks) : Future[Tasks] = {
    val updated = updateTask(operation, tasks, status = true)
    set(internalId, updated)
  }

  def reset(internalId: String, operation: UpdateOperation, tasks: Tasks) : Future[Tasks] = {
    val updated = updateTask(operation, tasks, status = false)
    set(internalId, updated)
  }

  private def updateTask(operation: UpdateOperation, tasks: Tasks, status: Boolean) = {
    operation match {
      case UpdateDetails => tasks.copy(details = status)
      case UpdatePersonalRepresentative => tasks.copy(personalRepresentative = status)
      case UpdateDeceased => tasks.copy(deceased = status)
      case UpdateTaxLiability => tasks.copy(yearsOfTaxLiability = status)
    }
  }

}
