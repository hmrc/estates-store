package uk.gov.hmrc.estatesstore.repositories

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers._
import uk.gov.hmrc.estatesstore.MongoSuite
import uk.gov.hmrc.estatesstore.models.register.{TaskCache, Tasks}

import scala.language.implicitConversions

class RegisterTasksRepositorySpec extends FreeSpec with MustMatchers
  with ScalaFutures with OptionValues with MongoSuite {

  "a tasks repository" - {

    val internalId = "Int-328969d0-557e-4559-96ba-074d0597107e"

    "must return None when no cache exists" in {
      running(application) {

        getConnection(application).map{ connection =>
          dropTheDatabase(connection)

          val repository = application.injector.instanceOf[MaintainTasksRepository]

          repository.get[TaskCache](internalId).futureValue mustBe None
        }
      }
    }

    "must set an updated Task and return one that exists for that user" in {
      running(application) {

        getConnection(application).map { connection =>
          dropTheDatabase(connection)

          val repository = application.injector.instanceOf[MaintainTasksRepository]

          val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

          val cache = TaskCache(internalId, task)

          val result = repository.set(internalId, cache).futureValue

          result mustBe true

          repository.get[TaskCache](internalId).futureValue.value.tasks mustBe task

          dropTheDatabase(connection)
        }

      }
    }

  }
}
