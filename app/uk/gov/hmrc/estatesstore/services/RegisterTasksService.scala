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

  def set(internalId: String, updated: Tasks) : Future[Tasks] = {
    val cache = TaskCache(internalId, updated)
    tasksRepository.set[TaskCache](internalId, cache).map(_ => updated)
  }

}