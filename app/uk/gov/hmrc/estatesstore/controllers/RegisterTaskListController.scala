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

package uk.gov.hmrc.estatesstore.controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.estatesstore.controllers.actions.IdentifierAction
import uk.gov.hmrc.estatesstore.models.register.Operations._
import uk.gov.hmrc.estatesstore.models.register.Tasks
import uk.gov.hmrc.estatesstore.services.RegisterTasksService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

class RegisterTaskListController @Inject()(
	cc: ControllerComponents,
	service: RegisterTasksService,
	authAction: IdentifierAction)(implicit ec: ExecutionContext) extends BackendController(cc) {

	private def updateTask(internalId: String, operation: UpdateOperation) = for {
		tasks <- service.get(internalId)
		updatedTasks <- service.updateTask(operation, tasks)
		savedTasks <- service.set(internalId, updatedTasks)
	} yield {
		Ok(Json.toJson(savedTasks))
	}

	def get(utr: String): Action[AnyContent] = authAction.async {
		request =>

			service.get(request.internalId).map {
				task =>
					Ok(Json.toJson(task))
			}
	}

	def set(utr: String): Action[JsValue] = authAction.async(parse.json) {
		request =>
			request.body.validate[Tasks] match {
				case JsSuccess(tasks, _) =>
					service.set(request.internalId, tasks).map {
						updated => Ok(Json.toJson(updated))
					}
				case _ => Future.successful(BadRequest)
			}
	}

	def completeDetails: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateDetails)
	}

	def completePersonalRepresentative: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdatePersonalRepresentative)
	}

	def completeDeceased: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateDeceased)
	}

	def completeTaxLiability: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateTaxLiability)
	}

}