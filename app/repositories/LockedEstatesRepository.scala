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

package repositories

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.{Configuration, Logging}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import models.claim_an_estate.EstateLock
import models.repository.StorageErrors
import reactivemongo.api.WriteConcern

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class LockedEstatesRepository @Inject()(mongo: MongoDriver,
                                        config: Configuration)
                                       (implicit ec: ExecutionContext) extends Logging{

  val collectionName: String = "claimAttempts"

  private val expireAfterSeconds = config.get[Int]("mongodb.expireAfterSeconds")

  private val lastUpdatedIndex = Index(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("estate-claims-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> expireAfterSeconds)
  )

  private def collection: Future[JSONCollection] =
    for {
      _   <- ensureIndexes
      res <- Future.successful(mongo.api.collection[JSONCollection](collectionName))
    } yield res

  private def ensureIndexes = for {
    collection              <- Future.successful(mongo.api.collection[JSONCollection](collectionName))
    lastUpdateIndexCreated  <- collection.indexesManager.ensure(lastUpdatedIndex)
  } yield {
    logger.info(s"[ensureIndexes] estate-claims-last-updated-index index newly created $lastUpdateIndexCreated")
    lastUpdateIndexCreated
  }

  def get(internalId: String): Future[Option[EstateLock]] =
    collection.flatMap(_.find(Json.obj("_id" -> internalId), projection = None).one[EstateLock])

  def remove(internalId: String): Future[Option[EstateLock]] =
    collection.flatMap(
      _.findAndRemove(
        Json.obj("_id" -> internalId),
        None,
        None,
        WriteConcern.Default,
        None,
        None,
        Seq.empty
      ).map(_.result[EstateLock])
    )

  def store(estateLock: EstateLock): Future[Either[StorageErrors, EstateLock]] = {

    val selector = Json.obj(
      "_id" -> estateLock.internalId
    )

    val modifier = Json.obj(
      "$set" -> estateLock
    )

    collection.flatMap(_.update.one(q = selector, u = modifier, upsert = true, multi = false)).map {
      case result if result.writeErrors.nonEmpty => Left(StorageErrors(result.writeErrors))
      case _ => Right(estateLock)
    }
  }
}