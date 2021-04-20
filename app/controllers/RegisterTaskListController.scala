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

package controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._
import controllers.actions.IdentifierAction
import models.register.Operations._
import models.register.Tasks
import services.RegisterTasksService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

class RegisterTaskListController @Inject()(
	cc: ControllerComponents,
	service: RegisterTasksService,
	authAction: IdentifierAction)(implicit ec: ExecutionContext) extends BackendController(cc) {

	private def updateTask(internalId: String, operation: UpdateOperation) = for {
		tasks <- service.get(internalId)
		savedTasks <- service.set(internalId, operation, tasks)
	} yield {
		Ok(Json.toJson(savedTasks))
	}

	private def resetTask(internalId: String, operation: UpdateOperation) = for {
		tasks <- service.get(internalId)
		savedTasks <- service.reset(internalId, operation, tasks)
	} yield {
		Ok(Json.toJson(savedTasks))
	}

	def get: Action[AnyContent] = authAction.async {
		request =>

			service.get(request.internalId).map {
				task =>
					Ok(Json.toJson(task))
			}
	}

	def setDefaultState: Action[JsValue] = authAction.async(parse.json) {
		request =>
			request.body.validate[Tasks] match {
				case JsSuccess(tasks, _) =>
					service.set(request.internalId, tasks).map {
						updated => Ok(Json.toJson(updated))
					}
				case _ => Future.successful(BadRequest)
			}
	}

	def setDetailsComplete: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateDetails)
	}

	def setPersonalRepresentativeComplete: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdatePersonalRepresentative)
	}

	def setDeceasedComplete: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateDeceased)
	}

	def setTaxLiabilityComplete: Action[AnyContent] = authAction.async {
		implicit request =>
			updateTask(request.internalId, UpdateTaxLiability)
	}

	def resetTaxLiability: Action[AnyContent] = authAction.async {
		implicit request =>
			resetTask(request.internalId, UpdateTaxLiability)
	}
}