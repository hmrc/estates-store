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

package uk.gov.hmrc.estatesstore.repositories

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.estatesstore.models.claim_an_estate.EstateClaim
import uk.gov.hmrc.estatesstore.models.repository.StorageErrors

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class ClaimedEstatesRepository @Inject()(mongo: MongoDriver,
                                         config: Configuration)
                                        (implicit ec: ExecutionContext) {

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
      res <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
    } yield res

  private lazy val ensureIndexes = for {
    collection              <- mongo.api.database.map(_.collection[JSONCollection](collectionName))
    lastUpdateIndexCreated  <- collection.indexesManager.ensure(lastUpdatedIndex)
  } yield lastUpdateIndexCreated

  def get(internalId: String): Future[Option[EstateClaim]] =
    collection.flatMap(_.find(Json.obj("_id" -> internalId), projection = None).one[EstateClaim])

  def remove(internalId: String): Future[Option[EstateClaim]] =
    collection.flatMap(_.findAndRemove(Json.obj("_id" -> internalId)).map(_.result[EstateClaim]))

  def store(estateClaim: EstateClaim): Future[Either[StorageErrors, EstateClaim]] = {

    val selector = Json.obj(
      "_id" -> estateClaim.internalId
    )

    val modifier = Json.obj(
      "$set" -> estateClaim
    )

    collection.flatMap(_.update.one(q = selector, u = modifier, upsert = true, multi = false)).map {
      case result if result.writeErrors.nonEmpty => Left(StorageErrors(result.writeErrors))
      case _ => Right(estateClaim)
    }
  }
}