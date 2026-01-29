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

package repositories

import com.mongodb.client.model.ReturnDocument
import config.AppConfig
import models.claim_an_estate.EstateLock
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class LockedEstatesRepository @Inject() (mongo: MongoComponent, config: AppConfig)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[EstateLock](
      mongoComponent = mongo,
      domainFormat = Format(EstateLock.reads, EstateLock.writes),
      collectionName = "claimAttempts",
      indexes = Seq(
        IndexModel(
          Indexes.ascending("lastUpdated"),
          IndexOptions()
            .name("estate-claims-last-updated-index")
            .expireAfter(config.expireAfterSeconds, TimeUnit.SECONDS)
            .unique(false)
        )
      )
    ) {

  private def byId(id: String) = Filters.eq("_id", id)

  def get(internalId: String): Future[Option[EstateLock]] =
    collection.find(byId(internalId)).headOption()

  def store(estateLock: EstateLock): Future[Option[EstateLock]] = {
    val selector = byId(estateLock.internalId)
    val options  = new FindOneAndReplaceOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER)

    collection.findOneAndReplace(selector, estateLock, options).headOption()
  }

}
