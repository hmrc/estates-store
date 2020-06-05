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

import java.time.LocalDateTime

import play.api.Configuration
import play.api.libs.json.{Json, Reads, Writes}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.estatesstore.formats.MongoDateTimeFormats

import scala.concurrent.{ExecutionContext, Future}

abstract class TasksRepository(mongo: ReactiveMongoApi, config: Configuration)
                               (implicit ec: ExecutionContext) {

  val collectionName: String

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val expireAfterSeconds = config.get[Int]("mongodb.expireAfterSeconds")

  private val lastUpdatedIndex = Index(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("tasks-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> expireAfterSeconds)
  )

  val started: Future[Unit] =
    collection.flatMap {
      coll =>
        for {
          _ <- coll.indexesManager.ensure(lastUpdatedIndex)
        } yield ()
    }

  def get[T](internalId: String)(implicit reads : Reads[T]): Future[Option[T]] = {
    val selector = Json.obj("internalId" -> internalId)

    val modifier = Json.obj(
      "$set" -> Json.obj(
        "lastUpdated" -> Json.toJson(LocalDateTime.now())(MongoDateTimeFormats.localDateTimeWrite)
      )
    )

    collection
      .flatMap(
        _.findAndUpdate(selector, modifier, fetchNewObject = true)
        .map(_.result[T])
      )
  }

  def set[T](internalId : String, updatedCache : T)(implicit writes: Writes[T]): Future[Boolean] = {

    val selector = Json.obj("internalId" -> internalId)

    val modifier = Json.obj(
      "$set" -> Json.toJson(updatedCache)
    )

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true, multi = false).map {
        result => result.ok
      }
    }
  }
}