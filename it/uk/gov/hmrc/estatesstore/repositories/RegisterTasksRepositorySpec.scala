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

          val repository = application.injector.instanceOf[RegisterTasksRepository]

          repository.get[TaskCache](internalId).futureValue mustBe None
        }
      }
    }

    "must return TaskCache when one exists" in {
      running(application) {

          getConnection(application).map { connection =>
            dropTheDatabase(connection)

            val repository = application.injector.instanceOf[RegisterTasksRepository]

            val task = Tasks(details = false, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

            val cache = TaskCache(internalId, task)

            val initial = repository.set(internalId, cache).futureValue

            initial mustBe true

            repository.get[TaskCache](internalId).futureValue.value.tasks mustBe task

            dropTheDatabase(connection)
        }
      }
    }

    "must set an updated Task and return one that exists for that user" in {
      running(application) {

        getConnection(application).map { connection =>
          dropTheDatabase(connection)

          val repository = application.injector.instanceOf[RegisterTasksRepository]

          val task = Tasks(details = true, personalRepresentative = false, deceased = false, yearsOfTaxLiability = false)

          val initial = TaskCache(internalId, task)

          repository.set(internalId, initial).futureValue

          repository.get[TaskCache](internalId).futureValue.value.tasks mustBe initial

          // update

          val updatedTask = Tasks(details = true, personalRepresentative = true, deceased = false, yearsOfTaxLiability = false)

          val updatedCache = TaskCache(internalId, updatedTask)

          repository.set(internalId, updatedCache).futureValue

          repository.get[TaskCache](internalId).futureValue.value.tasks mustBe updatedTask

          dropTheDatabase(connection)
        }

      }
    }

  }
}
