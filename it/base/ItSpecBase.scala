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

package base

import com.typesafe.config.ConfigFactory
import config.AppConfig
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.Configuration

class ItSpecBase extends AnyFreeSpec with Matchers with ScalaFutures with OptionValues with BeforeAndAfterEach {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(3, Seconds), interval = Span(15, Millis))

  val appConfig: AppConfig = new AppConfig(new Configuration(ConfigFactory.load()))
}
