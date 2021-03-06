/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.LocalDateTime

import org.scalatest.{FreeSpec, MustMatchers}
import play.api.libs.json.Json
import models.register.{TaskCache, Tasks}

class TaskCacheSpec extends FreeSpec with MustMatchers {

  "task must" - {

    "serialise to model" in {
      val cache = TaskCache("874872349",
        Tasks(
          details = true,
          personalRepresentative = false,
          deceased = false,
          yearsOfTaxLiability = false
        ),
        lastUpdated = LocalDateTime.of(2020, 10, 5, 12, 10)
      )

      val json = Json.parse(
        """
          |{
          | "internalId": "874872349",
          | "tasks": {
          |   "details": true,
          |   "personalRepresentative": false,
          |   "deceased": false,
          |   "yearsOfTaxLiability": false
          | },
          | "lastUpdated": {"$date":1601899800000}
          |}
          |""".stripMargin)

      json.as[TaskCache] mustBe cache
    }

    "deserialise to json" in {
      val cache = TaskCache("874872349",
        Tasks(
          details = true,
          personalRepresentative = false,
          deceased = false,
          yearsOfTaxLiability = false
        ),
        lastUpdated = LocalDateTime.of(2020, 10, 5, 12, 10)
      )

      val json = Json.toJson(cache)

      json mustBe Json.parse(
        """
          |{
          | "internalId": "874872349",
          | "tasks": {
          |   "details": true,
          |   "personalRepresentative": false,
          |   "deceased": false,
          |   "yearsOfTaxLiability": false
          | },
          | "lastUpdated": {"$date":1601899800000}
          |}
          |""".stripMargin)
    }

  }

}
