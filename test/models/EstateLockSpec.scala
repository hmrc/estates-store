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

package models

import base.SpecBase
import models.claim_an_estate.EstateLock
import play.api.libs.json.Json

import java.time.Instant

class EstateLockSpec extends SpecBase {

  "estate lock must" - {

    "default lastUpdated to now when the stored value cannot be parsed" in {
      val before = Instant.now

      val json = Json.parse("""
          |{
          | "_id": "874872349",
          | "utr": "1234567890",
          | "managedByAgent": true,
          | "estateLocked": false,
          | "lastUpdated":"not-an-instant"
          |}
          |""".stripMargin)

      val estateLock = json.as[EstateLock]
      val after      = Instant.now

      estateLock.internalId     mustBe "874872349"
      estateLock.utr            mustBe "1234567890"
      estateLock.managedByAgent mustBe true
      estateLock.estateLocked   mustBe false
      estateLock.lastUpdated      must (be >= before and be <= after)
    }
  }

}
