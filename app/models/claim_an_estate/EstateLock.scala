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

package models.claim_an_estate

import java.time.LocalDateTime

import play.api.libs.functional.syntax._
import play.api.libs.json._
import formats.MongoDateTimeFormats

case class EstateLock(internalId: String, utr: String, managedByAgent: Boolean, estateLocked: Boolean = false, lastUpdated: LocalDateTime = LocalDateTime.now) {
  def toResponse: JsObject =
    Json.obj(
      "internalId" -> this.internalId,
      "utr" -> this.utr,
      "managedByAgent" -> this.managedByAgent,
      "estateLocked" -> this.estateLocked
    )
}

object EstateLock extends MongoDateTimeFormats {
  implicit lazy val reads: Reads[EstateLock] = {
    (
        (__ \ "_id").read[String] and
        (__ \ "utr").read[String] and
        (__ \ "managedByAgent").read[Boolean] and
        (__ \ "estateLocked").read[Boolean] and
        (__ \ "lastUpdated").read(localDateTimeRead)
    ) (EstateLock.apply _)
  }

  implicit lazy val writes: OWrites[EstateLock] = {
    (
        (__ \ "_id").write[String] and
        (__ \ "utr").write[String] and
        (__ \ "managedByAgent").write[Boolean] and
        (__ \ "estateLocked").write[Boolean] and
        (__ \ "lastUpdated").write(localDateTimeWrite)
    ) (unlift(EstateLock.unapply))
  }
}
