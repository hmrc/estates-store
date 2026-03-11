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

package models.register

import models.MongoInstantReads
import play.api.libs.json.{OWrites, Reads, __}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class TaskCache(internalId: String, tasks: Tasks, lastUpdated: Instant = Instant.now)

object TaskCache {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[TaskCache] =
    (
      (__ \ "internalId").read[String] and
        (__ \ "tasks").read[Tasks] and
        // TODO this code should be only (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
        // but due to invalid data inserted in mongo before it has to pass 1 hour to expire old mongo data in prod to be able to put this in place
        // once that is done can delete test 'default lastUpdated to now when the stored value cannot be parsed' and remove MongoInstantReads
        MongoInstantReads.withNowFallback("lastUpdated")
    )(TaskCache.apply _)

  implicit lazy val writes: OWrites[TaskCache] =
    (
      (__ \ "internalId").write[String] and
        (__ \ "tasks").write[Tasks] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(TaskCache.unapply))

}
