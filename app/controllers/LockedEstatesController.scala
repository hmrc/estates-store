/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import controllers.actions.IdentifierAction
import models.claim_an_estate.responses.LockedEstateResponse._
import models.claim_an_estate.responses._
import models.responses.ErrorResponse
import models.responses.ErrorResponse._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.LockedEstatesService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class LockedEstatesController @Inject()(
                                         cc: ControllerComponents,
                                         service: LockedEstatesService,
                                         authAction: IdentifierAction)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def get(): Action[AnyContent] = authAction.async {
    implicit request =>

      service.get(request.internalId) map {
        case GetLockFound(estateLock) =>
          Ok(estateLock.toResponse)
        case GetLockNotFound =>
          NotFound(Json.toJson(ErrorResponse(NOT_FOUND, LOCKED_ESTATE_UNABLE_TO_LOCATE)))
      }
  }

  def store(): Action[JsValue] = authAction.async(parse.tolerantJson) {
    implicit request =>

      val maybeUtr = (request.body \ "utr").asOpt[String]
      val maybeManagedByAgent = (request.body \ "managedByAgent").asOpt[Boolean]
      val maybeEstateLocked = (request.body \ "estateLocked").asOpt[Boolean]
      val internalId = request.internalId

      service.store(internalId, maybeUtr, maybeManagedByAgent, maybeEstateLocked) map {
        case StoreSuccessResponse(estateLock) =>
          Created(estateLock.toResponse)
        case StoreParsingError =>
          BadRequest(Json.toJson(ErrorResponse(BAD_REQUEST, LOCKED_ESTATE_UNABLE_TO_PARSE)))
      }
  }

}
