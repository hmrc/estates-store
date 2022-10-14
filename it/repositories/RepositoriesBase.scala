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

import base.ItSpecBase
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters

import scala.concurrent.ExecutionContext.Implicits.global

class RepositoriesBase extends ItSpecBase {

  def remove(collection: MongoCollection[_], internalId: String): Unit =
    collection.deleteOne(Filters.eq("_id", internalId)).toFuture().map(_ => ()).futureValue

  def cleanupDB(collection: MongoCollection[_]): Unit =
    collection.deleteMany(new BsonDocument()).toFuture().futureValue
}
