/*
 * Copyright 2024 HM Revenue & Customs
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

package repositories

import com.mongodb.client.model.ReturnDocument
import config.AppConfig
import models.register.TaskCache
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EstateRegisterTasksRepository @Inject()(mongo: MongoComponent,
                                              config: AppConfig)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[TaskCache](
    mongoComponent = mongo,
    domainFormat = Format(TaskCache.reads, TaskCache.writes),
    collectionName = "registerTasks",
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions().name("tasks-last-updated-index")
          .expireAfter(config.expireAfterSeconds, TimeUnit.SECONDS)
          .unique(false))
    )
  ) {

  private def byId(id: String) = Filters.eq("internalId", id)

  def get(internalId: String): Future[Option[TaskCache]] = {
    val modifier = Updates.set("lastUpdated", LocalDateTime.now)

    val options = new FindOneAndUpdateOptions()
      .upsert(false)
      .returnDocument(ReturnDocument.AFTER)
    val selector = byId(internalId)

    collection.findOneAndUpdate(selector, modifier, options).headOption()
  }

  def set(internalId: String, updatedCache: TaskCache): Future[Boolean] = {
    val selector = byId(internalId)
    val options = new ReplaceOptions()
      .upsert(true)

    collection.replaceOne(selector, updatedCache, options).headOption().map(_.exists(_.wasAcknowledged()))
  }
}
